package com.sdgs.itb.service.community.impl;

import com.sdgs.itb.common.exceptions.DataNotFoundException;
import com.sdgs.itb.entity.goal.Goal;
import com.sdgs.itb.entity.goal.Scholar;
import com.sdgs.itb.entity.news.News;
import com.sdgs.itb.entity.news.NewsCategory;
import com.sdgs.itb.entity.news.NewsImage;
import com.sdgs.itb.entity.unit.Unit;
import com.sdgs.itb.infrastructure.community.dto.CommunityServiceImportDTO;
import com.sdgs.itb.infrastructure.goal.repository.GoalRepository;
import com.sdgs.itb.infrastructure.goal.repository.ScholarRepository;
import com.sdgs.itb.infrastructure.news.repository.NewsCategoryRepository;
import com.sdgs.itb.infrastructure.news.repository.NewsRepository;
import com.sdgs.itb.infrastructure.unit.repository.UnitRepository;
import com.sdgs.itb.service.community.CommunityServiceImportService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityServiceImportServiceImpl implements CommunityServiceImportService {

    private final NewsRepository newsRepository;
    private final NewsCategoryRepository newsCategoryRepository;
    private final GoalRepository goalRepository;
    private final UnitRepository unitRepository;
    private final ScholarRepository scholarRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    private final Map<String, Goal> goalCache = new HashMap<>();
    private final Map<String, NewsCategory> categoryCache = new HashMap<>();
    private final Map<String, Scholar> scholarCache = new HashMap<>();

    @Override
    public int importSample(int limit) {
        return importFromAPI(limit);
    }

    @Override
    public int importAll() {
        return importFromAPI(Integer.MAX_VALUE);
    }

    private String normalizeHashtagGoals(String goalsRaw) {
        if (goalsRaw == null || goalsRaw.isBlank()) return null;

        return Arrays.stream(goalsRaw.toUpperCase().split("\\s+"))
                .map(String::trim)
                .filter(s -> s.startsWith("#SDG"))
                .distinct()
                .sorted()
                .collect(Collectors.joining(" "));
    }

    private String normalizeTitle(String title) {
        return title.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private Map<String, Object> safeGet(URI uri) throws InterruptedException {

        int retries = 5;

        while (retries > 0) {
            try {
                return restTemplate.getForObject(uri, Map.class);

            } catch (org.springframework.web.client.HttpClientErrorException.TooManyRequests e) {

                System.out.println("⚠ Rate limited (429). Waiting 5 seconds...");
                Thread.sleep(5000);
                retries--;
            }
        }

        throw new RuntimeException("API rate limit exceeded after retries");
    }

    private int importFromAPI(int limit) {
        try {
            // Load caches
            if (goalCache.isEmpty()) {
                goalRepository.findAll().forEach(goal -> goalCache.put(goal.getTitle().toLowerCase(), goal));
            }
            if (categoryCache.isEmpty()) {
                newsCategoryRepository.findAll().forEach(cat -> categoryCache.put(cat.getCategory().toLowerCase(), cat));
            }

            if (scholarCache.isEmpty()) {
                scholarRepository.findAll().forEach(scholar -> scholarCache.put(scholar.getName().toLowerCase(), scholar));
            }

            NewsCategory communityCategory = categoryCache.get("community service");
            if (communityCategory == null) {
                throw new IllegalStateException("Category 'Community Service' not found!");
            }

            int page = 1;
            int perPage = 100;
            int importedCount = 0;

            while (importedCount < limit) {
                URI uri = new URI("https://pengabdian.dpmk.itb.ac.id/api/informations?page=" + page + "&per_page=" + perPage);
                Map<String, Object> response = restTemplate.getForObject(uri, Map.class);
                if (response == null) break;

                List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("data");
                if (items == null || items.isEmpty()) break;

                int newRecordsInThisPage = 0;

                for (Map<String, Object> item : items) {
                    if (importedCount >= limit) break;

                    CommunityServiceImportDTO dto = mapToDTO(item);
                    if (dto == null || dto.getSdgs().isEmpty()) continue;

                    // only import if created_date >= 2024
                    LocalDateTime created = LocalDateTime.parse(dto.getCreatedDate(),
                            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                    if (created.getYear() < 2024) continue;

                    // skip duplicates
                    if (newsRepository.findBySourceUrl(dto.getSourceUrl()).isPresent()) continue;

                    importFromDTO(dto);
                    importedCount++;
                    newRecordsInThisPage++;
                }

                // ✅ stop early if nothing new
                if (newRecordsInThisPage == 0) break;

                if (items.size() < perPage) break;
                page++;
            }

            System.out.println("Imported " + importedCount + " Community Service records.");
            return importedCount;
        } catch (Exception e) {
            throw new RuntimeException("Error importing Community Service: " + e.getMessage(), e);
        }
    }

    private CommunityServiceImportDTO mapToDTO(Map<String, Object> item) {
        String title = (String) item.get("title");
        String urlSlug = (String) item.get("slug");
        String content = (String) item.get("contex");
        String image = (String) item.get("url_image");
        String createdDate = (String) item.get("created_date");
        String goalsRaw = (String) item.get("goals");

        if (title == null || content == null || image == null || createdDate == null) {
            return null;
        }

//        String urlSlug = title.toLowerCase()
//                .replaceAll("[^a-z0-9\\s]", "")
//                .replaceAll("\\s+", "_");
        String sourceUrl = "https://pengabdian.dpmk.itb.ac.id/information/" + urlSlug;

        Set<String> sdgs = new LinkedHashSet<>();
        String sdgSource = goalsRaw != null && !goalsRaw.isBlank() ? goalsRaw : content;
        if (sdgSource != null) {
            // #SDG1, #SDG01, #sdg 1, SDG 01, etc.
            Pattern pattern = Pattern.compile("#SDG\\s*0?(\\d{1,2})", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(sdgSource);

            while (matcher.find()) {
                try {
                    int sdgNumber = Integer.parseInt(matcher.group(1));
                    // Only add valid SDG numbers (1–17)
                    if (sdgNumber >= 1 && sdgNumber <= 17) {
                        sdgs.add(String.valueOf(sdgNumber));
                    }
                } catch (NumberFormatException ignored) {
                    // Ignore malformed SDG references
                }
            }
        }

        if ((goalsRaw == null || goalsRaw.isBlank()) && sdgs.isEmpty()) {
            return null;
        }

        CommunityServiceImportDTO dto = new CommunityServiceImportDTO();
        dto.setTitle(title);
        dto.setSlug(urlSlug);
        dto.setContent(content);
        dto.setImage(image);
        dto.setSourceUrl(sourceUrl);
        dto.setCreatedDate(createdDate);
        dto.setHashtagGoals(normalizeHashtagGoals(goalsRaw));
        dto.setSdgs(new ArrayList<>(sdgs));
        return dto;
    }

    @Override
    public void importFromDTO(CommunityServiceImportDTO dto) {
        if (dto.getSourceUrl() == null || dto.getSdgs().isEmpty()) return;

        Optional<News> existingOpt = newsRepository.findBySourceUrl(dto.getSourceUrl());

        // UPDATE
        if (existingOpt.isPresent()) {
            News existing = existingOpt.get();

            String newHashtags = dto.getHashtagGoals();
            String oldHashtags = existing.getHashtagGoals();

            // nothing changed
            if (Objects.equals(newHashtags, oldHashtags)) {
                return;
            }

            System.out.println("Updating goals for News ID=" + existing.getId());

            // update hashtagGoals column
            existing.setHashtagGoals(newHashtags);

            // Replace goals safely
            existing.getNewsGoals().clear(); // orphanRemoval deletes old

            dto.getSdgs().forEach(sdgNum -> {
                Goal goal = goalCache.values().stream()
                        .filter(g -> g.getTitle()
                                .toUpperCase()
                                .startsWith("GOAL " + sdgNum))
                        .findFirst()
                        .orElse(null);

                if (goal != null) {
                    existing.addGoal(goal);
                }
            });

            newsRepository.save(existing);

            // If slug is missing, update it
//            if ((existing.getSlug() == null || existing.getSlug().isBlank())
//                    && dto.getSlug() != null && !dto.getSlug().isBlank()) {
//
//                existing.setSlug(dto.getSlug());
//                newsRepository.save(existing);
//            }

            // already exists → stop further processing
            return;
        }

        // CREATE
        News news = new News();
        news.setTitle(dto.getTitle());
        news.setSlug(dto.getSlug());
        news.setContent(dto.getContent());
        news.setThumbnailUrl(dto.getImage());
        news.setSourceUrl(dto.getSourceUrl());
        news.setHashtagGoals(dto.getHashtagGoals());

        Scholar outreach = scholarCache.get("outreach");
        news.setScholar(outreach);

        NewsCategory communityCategory = categoryCache.get("community service");
        news.setNewsCategory(communityCategory);

        if (dto.getCreatedDate() != null) {
            LocalDateTime localDateTime = LocalDateTime.parse(
                    dto.getCreatedDate(),
                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
            );
            LocalDate eventDate = localDateTime.toLocalDate();
            news.setEventDate(eventDate);
        } else {
            news.setEventDate(LocalDate.now());
        }

        News savedNews = newsRepository.save(news);

        // Add Unit safely
        Unit unit = unitRepository.findByAbbreviation("DPMK")
                .orElseThrow(() -> new DataNotFoundException("Unit with name 'DPMK' not found!"));
        savedNews.addUnit(unit);

        // Add Goals safely
        dto.getSdgs().stream()
                .distinct()
                .forEach(sdgNum -> {
                    Goal goal = goalCache.values().stream()
                            .filter(g -> g.getTitle().toUpperCase().startsWith("GOAL " + sdgNum))
                            .findFirst()
                            .orElse(null);
                    if (goal != null) savedNews.addGoal(goal);
                });

        newsRepository.save(savedNews);
    }

    @Override
    @Transactional
    public int migrateSourceUrlAndSlug() {

        String oldPrefix = "https://pengabdian.dpmk.itb.ac.id/information/";
        List<News> newsList = newsRepository.findBySourceUrlStartingWith(oldPrefix);

        int updatedCount = 0;

        for (News news : newsList) {

            boolean updated = false;

            // Ensure slug exists
            if (news.getSlug() == null || news.getSlug().isBlank()) {
                String slug = news.getTitle().toLowerCase()
                        .replaceAll("[^a-z0-9\\s]", "")
                        .replaceAll("\\s+", "_");
                news.setSlug(slug);
                updated = true;
            }

            // Ensure canonical sourceUrl
            String canonicalUrl = oldPrefix + news.getSlug();
            if (!canonicalUrl.equals(news.getSourceUrl())) {
                news.setSourceUrl(canonicalUrl);
                updated = true;
            }

            if (updated) {
                updatedCount++;
            }
        }

        newsRepository.saveAll(newsList);
        return updatedCount;
    }

    @Override
    @Transactional
    public int migrateHashtagGoals() {
        List<News> allNews = newsRepository.findAll();
        int updated = 0;

        for (News news : allNews) {

            // skip if already filled
            if (news.getHashtagGoals() != null &&
                    !news.getHashtagGoals().isBlank()) {
                continue;
            }

            if (news.getNewsGoals() == null || news.getNewsGoals().isEmpty()) {
                continue;
            }

            // build hashtag string from relations
            Set<String> hashtags = news.getNewsGoals().stream()
                    .map(ng -> ng.getGoal().getGoalNumber())
                    .sorted()
                    .map(num -> "#SDG" + num)
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            String hashtagGoals = String.join(" ", hashtags);

            news.setHashtagGoals(hashtagGoals);
            updated++;
        }

        newsRepository.saveAll(allNews);

        return updated;
    }

    @Override
    @Transactional
    public int migrateSlug() {

        try {

            int page = 1;
            int perPage = 100;
            int updated = 0;

            // ✅ Only load news that NEED slug
            Map<String, News> newsByTitle =
                    newsRepository.findAll().stream()
                            .filter(n -> n.getSlug() == null || n.getSlug().isBlank())
                            .collect(Collectors.toMap(
                                    n -> normalizeTitle(n.getTitle()),
                                    n -> n,
                                    (a, b) -> a
                            ));

            if (newsByTitle.isEmpty()) {
                return 0;
            }

            System.out.println("Need slug update: " + newsByTitle.size());

            while (!newsByTitle.isEmpty()) {

                URI uri = new URI(
                        "https://pengabdian.dpmk.itb.ac.id/api/informations?page="
                                + page + "&per_page=" + perPage
                );

                Map<String, Object> response =
                        restTemplate.getForObject(uri, Map.class);

                if (response == null) break;

                List<Map<String, Object>> items =
                        (List<Map<String, Object>>) response.get("data");

                if (items == null || items.isEmpty()) break;

                int foundThisPage = 0;

                for (Map<String, Object> item : items) {

                    String title = (String) item.get("title");
                    String slug = (String) item.get("slug");

                    if (title == null || slug == null) continue;

                    String key = normalizeTitle(title);

                    News news = newsByTitle.remove(key);

                    if (news != null) {
                        news.setSlug(slug);
                        updated++;
                        foundThisPage++;
                    }
                }

                System.out.println(
                        "Page " + page +
                                " | updated=" + updated +
                                " | remaining=" + newsByTitle.size()
                );

                // ✅ SAME OPTIMIZATION AS IMPORT
                if (foundThisPage == 0) {
                    System.out.println("No matches found → stopping early");
                    break;
                }

                if (items.size() < perPage) break;

                page++;
            }

            newsRepository.flush();

            return updated;

        } catch (Exception e) {
            throw new RuntimeException("Failed migrating slug from DPMK API", e);
        }
    }
}
