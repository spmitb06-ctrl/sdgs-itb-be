package com.sdgs.itb.service.news.impl;

import com.sdgs.itb.entity.goal.Goal;
import com.sdgs.itb.entity.goal.Scholar;
import com.sdgs.itb.entity.news.News;
import com.sdgs.itb.entity.news.NewsCategory;
import com.sdgs.itb.entity.unit.Unit;
import com.sdgs.itb.infrastructure.goal.repository.GoalRepository;
import com.sdgs.itb.infrastructure.goal.repository.ScholarRepository;
import com.sdgs.itb.infrastructure.news.repository.NewsCategoryRepository;
import com.sdgs.itb.infrastructure.news.repository.NewsRepository;
import com.sdgs.itb.infrastructure.unit.repository.UnitRepository;
import com.sdgs.itb.infrastructure.wordpress.config.WordPressSyncProperties;
import com.sdgs.itb.infrastructure.wordpress.dto.WordPressPostDTO;
import com.sdgs.itb.service.news.WordPressNewsSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class WordPressNewsSyncServiceImpl implements WordPressNewsSyncService {

    private static final Long DEFAULT_NEWS_CATEGORY_ID = 1L;
    private static final Long DEFAULT_SCHOLAR_ID = 4L;
    private static final Pattern SDG_TAG_PATTERN = Pattern.compile("(?i)tag-sdg(\\d+)");
    private static final Pattern FRONT_GOAL_PATTERN = Pattern.compile("(?i)^\\s*goal\\s*(\\d{1,2})");

    private final NewsRepository newsRepository;
    private final UnitRepository unitRepository;
    private final GoalRepository goalRepository;
    private final NewsCategoryRepository newsCategoryRepository;
    private final ScholarRepository scholarRepository;
    private final WordPressSyncProperties syncProperties;

    private final RestClient restClient = RestClient.builder()
            .defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) SDGs-ITB/1.0")
            .build();

    private final Map<Integer, Goal> goalByNumberCache = new HashMap<>();
    private final Map<String, Unit> unitByAbbreviationCache = new HashMap<>();
    private NewsCategory defaultCategoryCache;
    private Scholar defaultScholarCache;

    private synchronized void initCaches() {
        if (goalByNumberCache.isEmpty()) {
            List<Goal> goals = goalRepository.findAll();
            for (Goal g : goals) {
                Integer num = g.getGoalNumber();
                if (num == null) {
                    num = extractGoalNumberFromTitle(g.getTitle());
                }
                if (num != null && num >= 1 && num <= 17) {
                    goalByNumberCache.put(num, g);
                }
            }
        }

        if (defaultCategoryCache == null) {
            defaultCategoryCache = newsCategoryRepository.findById(DEFAULT_NEWS_CATEGORY_ID)
                    .orElseThrow(() -> new IllegalStateException("NewsCategory with ID " + DEFAULT_NEWS_CATEGORY_ID + " not found"));
        }

        if (defaultScholarCache == null) {
            defaultScholarCache = scholarRepository.findById(DEFAULT_SCHOLAR_ID)
                    .orElseThrow(() -> new IllegalStateException("Scholar with ID " + DEFAULT_SCHOLAR_ID + " not found"));
        }
    }

    private Integer extractGoalNumberFromTitle(String title) {
        if (title == null || title.isBlank()) return null;
        Matcher m = FRONT_GOAL_PATTERN.matcher(title.trim());
        if (m.find()) {
            try {
                return Integer.parseInt(m.group(1));
            } catch (NumberFormatException ignored) {}
        }
        return null;
    }

    private Unit resolveUnit(String abbreviation) {
        if (abbreviation == null || abbreviation.isBlank()) return null;
        String key = abbreviation.trim().toLowerCase();
        return unitByAbbreviationCache.computeIfAbsent(key, k ->
                unitRepository.findByAbbreviation(abbreviation.trim()).orElse(null)
        );
    }

    @Override
    public Map<String, Object> syncAllConfiguredSources() {
        initCaches();
        Map<String, Object> results = new LinkedHashMap<>();
        int grandTotal = 0;

        for (WordPressSyncProperties.WordPressSourceConfig source : syncProperties.getSources()) {
            try {
                log.info("Starting WordPress sync from source: '{}' ({})", source.getName(), source.getUrl());
                int count = syncFromEndpoint(source.getUrl(), source.getUnitAbbreviation());
                results.put(source.getName(), Map.of("status", "SUCCESS", "imported", count));
                grandTotal += count;
            } catch (Exception e) {
                log.error("Failed to sync from source: {}", source.getName(), e);
                results.put(source.getName(), Map.of("status", "FAILED", "error", e.getMessage()));
            }
        }

        results.put("grandTotalImported", grandTotal);
        return results;
    }

    @Override
    @Transactional
    public int syncFromEndpoint(String endpointUrl, String unitAbbreviation) {
        initCaches();

        String url = endpointUrl.contains("_embed") ? endpointUrl : endpointUrl + (endpointUrl.contains("?") ? "&_embed" : "?_embed");

        List<WordPressPostDTO> posts;
        try {
            posts = restClient.get()
                    .uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<WordPressPostDTO>>() {});
        } catch (Exception e) {
            log.error("Failed to fetch posts from WordPress API: {}", url, e);
            throw new RuntimeException("Could not fetch posts from " + url + ": " + e.getMessage(), e);
        }

        if (posts == null || posts.isEmpty()) {
            return 0;
        }

        Unit targetUnit = resolveUnit(unitAbbreviation);
        if (targetUnit == null) {
            log.warn("Unit with abbreviation '{}' not found in sdgs.units", unitAbbreviation);
        }

        int importedCount = 0;

        for (WordPressPostDTO post : posts) {
            String link = post.getLink() != null ? post.getLink().trim() : null;
            if (link == null || link.isBlank()) {
                continue;
            }

            // 1. Idempotency Check: lookup existing by sourceUrl
            Optional<News> existingOpt = newsRepository.findBySourceUrl(link);
            News news = existingOpt.orElseGet(News::new);

            // 2. Map Title & Content (from excerpt.rendered, fallback to content.rendered)
            String title = post.getTitle() != null ? post.getTitle().getRendered() : "";
            title = cleanHtmlEntities(title);

            String excerptContent = "";
            if (post.getExcerpt() != null && post.getExcerpt().getRendered() != null && !post.getExcerpt().getRendered().isBlank()) {
                excerptContent = post.getExcerpt().getRendered().trim();
            } else if (post.getContent() != null && post.getContent().getRendered() != null) {
                excerptContent = post.getContent().getRendered().trim();
            }

            news.setTitle(title);
            news.setContent(excerptContent);
            news.setSourceUrl(link);
            news.setNewsCategory(defaultCategoryCache);
            news.setScholar(defaultScholarCache);

            // Extract category name into temp_category
            news.setTempCategory(extractCategoryName(post));

            // 3. Slug
            if (post.getSlug() != null && !post.getSlug().isBlank()) {
                news.setSlug(post.getSlug());
            } else if (news.getSlug() == null || news.getSlug().isBlank()) {
                news.setSlug(title.toLowerCase().replaceAll("[^a-z0-9\\s-]", "").replaceAll("\\s+", "-"));
            }

            // 4. Parse eventDate & scholarYear from 'date' (ISO-8601)
            if (post.getDate() != null && !post.getDate().isBlank()) {
                try {
                    LocalDateTime dt = LocalDateTime.parse(post.getDate(), DateTimeFormatter.ISO_DATE_TIME);
                    news.setEventDate(dt.toLocalDate());
                    news.setScholarYear(String.valueOf(dt.getYear()));
                } catch (Exception ex) {
                    news.setEventDate(LocalDate.now());
                    news.setScholarYear(String.valueOf(LocalDate.now().getYear()));
                }
            } else if (news.getEventDate() == null) {
                news.setEventDate(LocalDate.now());
                news.setScholarYear(String.valueOf(LocalDate.now().getYear()));
            }

            // 5. Featured Media Extraction
            String thumbnail = extractFeaturedMediaUrl(post);
            if (thumbnail != null) {
                news.setThumbnailUrl(thumbnail);
            }

            // 6. Match SDG Goals from class_list (e.g., tag-sdg04 -> Goal 4)
            Set<Goal> matchedGoals = extractGoalsFromClassList(post.getClassList());
            for (Goal g : matchedGoals) {
                news.addGoal(g);
            }

            // 7. Associate Unit (FITB)
            if (targetUnit != null) {
                news.addUnit(targetUnit);
            }

            newsRepository.save(news);
            importedCount++;
        }

        log.info("Finished sync for '{}' ({}): imported/updated {} news items", unitAbbreviation, endpointUrl, importedCount);
        return importedCount;
    }

    private String extractFeaturedMediaUrl(WordPressPostDTO post) {
        if (post.getEmbedded() != null && post.getEmbedded().getFeaturedMedia() != null) {
            for (WordPressPostDTO.FeaturedMedia media : post.getEmbedded().getFeaturedMedia()) {
                if (media.getSourceUrl() != null && !media.getSourceUrl().isBlank()) {
                    return media.getSourceUrl();
                }
                if (media.getGuid() != null && media.getGuid().getRendered() != null && !media.getGuid().getRendered().isBlank()) {
                    return media.getGuid().getRendered();
                }
            }
        }

        if (post.getLinks() != null && post.getLinks().containsKey("wp:featuredmedia")) {
            List<WordPressPostDTO.LinkItem> mediaLinks = post.getLinks().get("wp:featuredmedia");
            if (mediaLinks != null && !mediaLinks.isEmpty() && mediaLinks.get(0).getHref() != null) {
                String mediaHref = mediaLinks.get(0).getHref();
                try {
                    Map<?, ?> mediaObj = restClient.get()
                            .uri(mediaHref)
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .body(Map.class);
                    if (mediaObj != null && mediaObj.containsKey("source_url")) {
                        return (String) mediaObj.get("source_url");
                    }
                } catch (Exception ex) {
                    log.debug("No featured media found at secondary href: {}", mediaHref);
                }
            }
        }

        return null;
    }

    private String extractCategoryName(WordPressPostDTO post) {
        if (post.getEmbedded() != null && post.getEmbedded().getTerms() != null) {
            for (List<WordPressPostDTO.TermItem> termList : post.getEmbedded().getTerms()) {
                if (termList != null) {
                    for (WordPressPostDTO.TermItem term : termList) {
                        if ("category".equalsIgnoreCase(term.getTaxonomy()) && term.getName() != null) {
                            return cleanHtmlEntities(term.getName());
                        }
                    }
                }
            }
        }

        if (post.getLinks() != null && post.getLinks().containsKey("wp:term")) {
            List<WordPressPostDTO.LinkItem> termLinks = post.getLinks().get("wp:term");
            if (termLinks != null) {
                for (WordPressPostDTO.LinkItem linkItem : termLinks) {
                    if ("category".equalsIgnoreCase(linkItem.getTaxonomy()) && linkItem.getHref() != null) {
                        try {
                            List<Map<String, Object>> terms = restClient.get()
                                    .uri(linkItem.getHref())
                                    .retrieve()
                                    .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
                            if (terms != null && !terms.isEmpty() && terms.get(0).containsKey("name")) {
                                return cleanHtmlEntities((String) terms.get(0).get("name"));
                            }
                        } catch (Exception ignored) {}
                    }
                }
            }
        }

        return null;
    }

    private Set<Goal> extractGoalsFromClassList(List<String> classList) {
        Set<Goal> matched = new HashSet<>();
        if (classList == null || classList.isEmpty()) {
            return matched;
        }

        for (String cls : classList) {
            if (cls == null) continue;
            Matcher m = SDG_TAG_PATTERN.matcher(cls.trim());
            if (m.find()) {
                try {
                    int goalNum = Integer.parseInt(m.group(1));
                    Goal goal = goalByNumberCache.get(goalNum);
                    if (goal != null) {
                        matched.add(goal);
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return matched;
    }

    private String cleanHtmlEntities(String text) {
        if (text == null) return "";
        return text.replace("&#8211;", "-")
                .replace("&#8212;", "—")
                .replace("&#8216;", "'")
                .replace("&#8217;", "'")
                .replace("&#8220;", "\"")
                .replace("&#8221;", "\"")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .trim();
    }
}