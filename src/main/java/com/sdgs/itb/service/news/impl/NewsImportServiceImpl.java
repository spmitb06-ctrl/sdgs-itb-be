package com.sdgs.itb.service.news.impl;

import com.sdgs.itb.entity.news.News;
import com.sdgs.itb.entity.news.NewsCategory;
import com.sdgs.itb.entity.goal.Goal;
import com.sdgs.itb.entity.goal.Scholar;
import com.sdgs.itb.entity.unit.Unit;
import com.sdgs.itb.infrastructure.news.repository.NewsCategoryRepository;
import com.sdgs.itb.infrastructure.news.repository.NewsRepository;
import com.sdgs.itb.infrastructure.goal.repository.GoalRepository;
import com.sdgs.itb.infrastructure.goal.repository.ScholarRepository;
import com.sdgs.itb.infrastructure.typesense.dto.TypesenseNewsExportDTO;
import com.sdgs.itb.infrastructure.unit.repository.UnitRepository;
import com.sdgs.itb.service.news.NewsImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NewsImportServiceImpl implements NewsImportService {

    private final ScholarRepository scholarRepository;
    private final GoalRepository goalRepository;
    private final NewsRepository newsRepository;
    private final NewsCategoryRepository newsCategoryRepository;
    private final UnitRepository unitRepository;

    private final Map<String, Goal> goalCache = new HashMap<>();
    private final Map<String, Scholar> scholarCache = new HashMap<>();
    private final Map<String, NewsCategory> categoryCache = new HashMap<>();

    @Override
    public News importFromTypesense(TypesenseNewsExportDTO dto) {
        // ---- Initialize caches ----
        if (goalCache.isEmpty()) {
            goalRepository.findAll().forEach(goal ->
                    goalCache.put(goal.getTitle().toLowerCase(), goal)
            );
        }
        if (scholarCache.isEmpty()) {
            scholarRepository.findAll().forEach(scholar ->
                    scholarCache.put(scholar.getName().toLowerCase(), scholar)
            );
        }
        if (categoryCache.isEmpty()) {
            newsCategoryRepository.findAll().forEach(category ->
                    categoryCache.put(category.getCategory().toLowerCase(), category)
            );
        }

        // ---- Deduplication: check if news already exists ----
        Optional<News> existingOpt = newsRepository.findBySourceUrl(dto.getUrl());

        if (existingOpt.isPresent()) {
            News existing = existingOpt.get();

            // Add any missing goals
            if (dto.getSdg() != null && !dto.getSdg().isEmpty()) {
                dto.getSdg().stream()
                        .map(String::toLowerCase)
                        .distinct()
                        .forEach(sdgName -> {
                            Goal goal = goalCache.get(sdgName);
                            if (goal != null) {
                                existing.addGoal(goal);
                            }
                        });
            }

            return newsRepository.save(existing);
        }

        // ---- Create new news ----
        News newNews = new News();
        newNews.setTitle(dto.getTitle());
        newNews.setThumbnailUrl(dto.getImage());
        newNews.setScholarYear(dto.getYear());

        // ✅ Use dateTime (unified field from Typesense import)
        newNews.setEventDate(dto.getDateTime() != null
                ? dto.getDateTime()
                : LocalDate.now());

        // ---- Category mapping ----
        String scholarName = dto.getScholarName().toLowerCase();
        NewsCategory category;
        if (scholarName.equals("project")) {
            category = categoryCache.get("research");
            newNews.setContent(dto.getAbstractText());
            newNews.setSourceUrl("https://scholar.itb.ac.id/project_detail/" + dto.getUrl());
            newNews.setThumbnailUrl("/news/project.jpg");
        } else if (scholarName.equals("paper") || scholarName.equals("patent") || scholarName.equals("thesis")) {
            category = categoryCache.get("publication");
            newNews.setContent(dto.getAbstractText());
            switch (scholarName) {
                case "paper" -> {
                    newNews.setSourceUrl("https://scholar.itb.ac.id/paper_detail/" + dto.getUrl());
                    newNews.setThumbnailUrl("/news/paper.jpeg");
                }
                case "patent" -> {
                    newNews.setSourceUrl("https://scholar.itb.ac.id/patent_detail/" + dto.getUrl());
                    newNews.setThumbnailUrl("/news/patent.jpg");
                }
                case "thesis" -> {
                    newNews.setSourceUrl("https://scholar.itb.ac.id/thesis_detail/" + dto.getUrl());
                    newNews.setThumbnailUrl("/news/thesis.jpeg");
                }
            }
        } else {
            category = categoryCache.get("community service");
            newNews.setContent(dto.getAbstractText());
            newNews.setSourceUrl("https://scholar.itb.ac.id/outreach_detail/" + dto.getUrl());
            newNews.setThumbnailUrl("/news/outreach.jpg");
        }

        if (category == null) {
            throw new IllegalStateException("No matching category found for: " + dto.getScholarName());
        }
        newNews.setNewsCategory(category);

        // ---- Scholar mapping ----
        Scholar scholar = scholarCache.get(dto.getScholarName().toLowerCase());
        if (scholar == null) {
            throw new IllegalArgumentException("Scholar not found: " + dto.getScholarName());
        }
        newNews.setScholar(scholar);

        // Save first to get ID
        News savedNews = newsRepository.save(newNews);

        // ---- Add goals ----
        if (dto.getSdg() != null && !dto.getSdg().isEmpty()) {
            dto.getSdg().stream()
                    .map(String::toLowerCase)
                    .distinct()
                    .forEach(sdgName -> {
                        Goal goal = goalCache.get(sdgName);
                        if (goal != null) {
                            savedNews.addGoal(goal);
                        }
                    });
        }

        // ---- Add Units based on organization IDs ----
        if (dto.getOrganizations() != null && !dto.getOrganizations().isEmpty()) {
            for (Long orgId : dto.getOrganizations()) {
                List<Unit> units = unitRepository.findByOrganizationId(orgId);
                for (Unit unit : units) {
                    savedNews.addUnit(unit);
                }
            }
        }

        return newsRepository.save(savedNews);
    }
}
