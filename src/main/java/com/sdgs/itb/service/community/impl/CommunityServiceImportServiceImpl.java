package com.sdgs.itb.service.community.impl;

import com.sdgs.itb.common.exceptions.DataNotFoundException;
import com.sdgs.itb.entity.news.News;
import com.sdgs.itb.entity.news.NewsCategory;
import com.sdgs.itb.entity.goal.Goal;
import com.sdgs.itb.entity.news.NewsUnit;
import com.sdgs.itb.entity.unit.Unit;
import com.sdgs.itb.infrastructure.news.repository.NewsCategoryRepository;
import com.sdgs.itb.infrastructure.news.repository.NewsRepository;
import com.sdgs.itb.infrastructure.goal.repository.GoalRepository;
import com.sdgs.itb.infrastructure.community.dto.CommunityServiceImportDTO;
import com.sdgs.itb.infrastructure.unit.repository.UnitRepository;
import com.sdgs.itb.service.community.CommunityServiceImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CommunityServiceImportServiceImpl implements CommunityServiceImportService {

    private final NewsRepository newsRepository;
    private final NewsCategoryRepository newsCategoryRepository;
    private final GoalRepository goalRepository;
    private final UnitRepository unitRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    private final Map<String, Goal> goalCache = new HashMap<>();
    private final Map<String, NewsCategory> categoryCache = new HashMap<>();

    private static final Map<String, String> SDG_MAP = Map.ofEntries(
            Map.entry("#SDG1", "GOAL 1: No Poverty"),
            Map.entry("#SDG2", "GOAL 2 : Zero Hunger"),
            Map.entry("#SDG3", "GOAL 3: Good Health and Well-being"),
            Map.entry("#SDG4", "GOAL 4: Quality Education"),
            Map.entry("#SDG5", "GOAL 5: Gender Equality"),
            Map.entry("#SDG6", "GOAL 6: Clean Water and Sanitation"),
            Map.entry("#SDG7", "GOAL 7: Affordable and Clean Energy"),
            Map.entry("#SDG8", "GOAL 8: Decent Work and Economic"),
            Map.entry("#SDG9", "GOAL 9: Industry, Innovation and Infrastructure"),
            Map.entry("#SDG10", "GOAL 10: Reduced Inequality"),
            Map.entry("#SDG11", "GOAL 11: Sustainable Cities and Communities"),
            Map.entry("#SDG12", "GOAL 12: Responsible Consumption and Production"),
            Map.entry("#SDG13", "GOAL 13: Climate Action"),
            Map.entry("#SDG14", "GOAL 14: Life Below Water"),
            Map.entry("#SDG15", "GOAL 15: Life on Land"),
            Map.entry("#SDG16", "GOAL 16: Peace and Justice Strong Institutions"),
            Map.entry("#SDG17", "GOAL 17: Partnership for the Goal")
    );

    @Override
    public void importSample(int limit) {
        importFromAPI(limit);
    }

    @Override
    public void importAll() {
        importFromAPI(Integer.MAX_VALUE);
    }

    private void importFromAPI(int limit) {
        try {
            // Initialize caches
            if (goalCache.isEmpty()) {
                goalRepository.findAll().forEach(goal -> goalCache.put(goal.getTitle().toLowerCase(), goal));
            }
            if (categoryCache.isEmpty()) {
                newsCategoryRepository.findAll().forEach(cat -> categoryCache.put(cat.getCategory().toLowerCase(), cat));
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

                // API response as Map
                Map<String, Object> response = restTemplate.getForObject(uri, Map.class);
                if (response == null) break;

                List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("data");
                if (items == null || items.isEmpty()) break;

                for (Map<String, Object> item : items) {
                    if (importedCount >= limit) break;

                    CommunityServiceImportDTO dto = mapToDTO(item);
                    if (dto.getSdgs().isEmpty()) continue;

                    importFromDTO(dto);
                    importedCount++;
                }

                if (items.size() < perPage) break;
                page++;
            }

            System.out.println("Imported " + importedCount + " Community Service records.");
        } catch (Exception e) {
            throw new RuntimeException("Error importing Community Service: " + e.getMessage(), e);
        }
    }

    private CommunityServiceImportDTO mapToDTO(Map<String, Object> item) {
        String title = (String) item.get("title");
        String content = (String) item.get("contex");
        String image = (String) item.get("url_image");
        String createdDate = (String) item.get("created_date");
        String goalsRaw = (String) item.get("goals");

        // Skip if any mandatory field is null
        if (title == null || content == null || image == null || createdDate == null) {
            return null; // will be ignored in importFromAPI
        }

        // Build URL slug
        String urlSlug = title.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "_");
        String sourceUrl = "https://pengabdian.dpmk.itb.ac.id/information/" + urlSlug;

        // Extract SDGs → store only numbers
        List<String> sdgs = new ArrayList<>();
        String sdgSource = goalsRaw != null && !goalsRaw.isBlank() ? goalsRaw : content;

        if (sdgSource != null) {
            Pattern pattern = Pattern.compile("#SDG(\\d{1,2})");
            Matcher matcher = pattern.matcher(sdgSource);
            while (matcher.find()) {
                sdgs.add(matcher.group(1)); // capture only number
            }
        }

        // Skip if both goalsRaw is null AND no SDGs found in content
        if ((goalsRaw == null || goalsRaw.isBlank()) && sdgs.isEmpty()) {
            return null;
        }

        CommunityServiceImportDTO dto = new CommunityServiceImportDTO();
        dto.setTitle(title);
        dto.setContent(content);
        dto.setImage(image);
        dto.setSourceUrl(sourceUrl);
        dto.setCreatedDate(createdDate);
        dto.setSdgs(sdgs);

        return dto;
    }

    @Override
    public void importFromDTO(CommunityServiceImportDTO dto) {
        if (dto.getSourceUrl() == null || dto.getSdgs().isEmpty()) return;

        Optional<News> existingOpt = newsRepository.findBySourceUrl(dto.getSourceUrl());
        News news = existingOpt.orElseGet(News::new);

        // Set basic fields
        news.setTitle(dto.getTitle());
        news.setContent(dto.getContent());
        news.setThumbnailUrl(dto.getImage());
        news.setSourceUrl(dto.getSourceUrl());

        NewsCategory communityCategory = categoryCache.get("community service");
        news.setNewsCategory(communityCategory);

        if (dto.getCreatedDate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            LocalDateTime localCreated = LocalDateTime.parse(dto.getCreatedDate(), formatter);
            OffsetDateTime created = localCreated.atOffset(ZoneOffset.UTC);

            news.setCreatedAt(created);
            news.setUpdatedAt(created);
        } else {
            news.setCreatedAt(OffsetDateTime.now());
            news.setUpdatedAt(OffsetDateTime.now());
        }

        // Save first to generate ID (required for Many-to-Many)
        News savedNews = newsRepository.save(news);

        Unit unit = unitRepository.findByName("Other")
                .orElseThrow(() -> new DataNotFoundException("Unit with name 'Other' not found!"));

        NewsUnit af = NewsUnit.builder().news(savedNews).unit(unit).build();
        savedNews.getNewsUnits().add(af);

        // Match goals by number instead of full title
        dto.getSdgs().stream()
                .distinct()
                .forEach(sdgNum -> {
                    Goal goal = goalCache.values().stream()
                            .filter(g -> g.getTitle().toUpperCase().startsWith("GOAL " + sdgNum))
                            .findFirst()
                            .orElse(null);
                    if (goal != null) savedNews.addGoal(goal);
                });

        // Save again to persist goals
        newsRepository.save(savedNews);
    }
}