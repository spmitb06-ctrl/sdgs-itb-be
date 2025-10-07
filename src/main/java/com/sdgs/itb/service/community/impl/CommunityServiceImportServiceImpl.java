package com.sdgs.itb.service.community.impl;

import com.sdgs.itb.common.exceptions.DataNotFoundException;
import com.sdgs.itb.entity.goal.Goal;
import com.sdgs.itb.entity.news.News;
import com.sdgs.itb.entity.news.NewsCategory;
import com.sdgs.itb.entity.unit.Unit;
import com.sdgs.itb.infrastructure.community.dto.CommunityServiceImportDTO;
import com.sdgs.itb.infrastructure.goal.repository.GoalRepository;
import com.sdgs.itb.infrastructure.news.repository.NewsCategoryRepository;
import com.sdgs.itb.infrastructure.news.repository.NewsRepository;
import com.sdgs.itb.infrastructure.unit.repository.UnitRepository;
import com.sdgs.itb.service.community.CommunityServiceImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
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

    @Override
    public int importSample(int limit) {
        return importFromAPI(limit);
    }

    @Override
    public int importAll() {
        return importFromAPI(Integer.MAX_VALUE);
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
        String content = (String) item.get("contex");
        String image = (String) item.get("url_image");
        String createdDate = (String) item.get("created_date");
        String goalsRaw = (String) item.get("goals");

        if (title == null || content == null || image == null || createdDate == null) {
            return null;
        }

        String urlSlug = title.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "_");
        String sourceUrl = "https://pengabdian.dpmk.itb.ac.id/information/" + urlSlug;

        List<String> sdgs = new ArrayList<>();
        String sdgSource = goalsRaw != null && !goalsRaw.isBlank() ? goalsRaw : content;
        if (sdgSource != null) {
            Matcher matcher = Pattern.compile("#SDG(\\d{1,2})").matcher(sdgSource);
            while (matcher.find()) {
                sdgs.add(matcher.group(1));
            }
        }

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
        if (existingOpt.isPresent()) {
            // already exists → skip
            return;
        }

        News news = new News();
        news.setTitle(dto.getTitle());
        news.setContent(dto.getContent());
        news.setThumbnailUrl(dto.getImage());
        news.setSourceUrl(dto.getSourceUrl());

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
}
