package com.sdgs.itb.service.news.impl;

import com.sdgs.itb.entity.news.Article;
import com.sdgs.itb.entity.news.ArticleCategory;
import com.sdgs.itb.entity.sdgs.Goal;
import com.sdgs.itb.entity.sdgs.Scholar;
import com.sdgs.itb.infrastructure.news.repository.ArticleCategoryRepository;
import com.sdgs.itb.infrastructure.news.repository.ArticleRepository;
import com.sdgs.itb.infrastructure.sdgs.repository.GoalRepository;
import com.sdgs.itb.infrastructure.sdgs.repository.ScholarRepository;
import com.sdgs.itb.infrastructure.typesense.dto.TypesenseArticleExportDTO;
import com.sdgs.itb.service.news.ArticleImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleImportServiceImpl implements ArticleImportService {

    private final ScholarRepository scholarRepository;
    private final GoalRepository goalRepository;
    private final ArticleRepository articleRepository;
    private final ArticleCategoryRepository articleCategoryRepository;

    private final Map<String, Goal> goalCache = new HashMap<>();
    private final Map<String, Scholar> scholarCache = new HashMap<>();
    private final Map<String, ArticleCategory> categoryCache = new HashMap<>();

    @Override
    public Article importFromTypesense(TypesenseArticleExportDTO dto) {
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
            articleCategoryRepository.findAll().forEach(category ->
                    categoryCache.put(category.getCategory().toLowerCase(), category)
            );
        }

        // ---- Deduplication: check if article already exists ----
        Optional<Article> existingOpt = articleRepository.findBySourceUrl(dto.getUrl());

        if (existingOpt.isPresent()) {
            Article existing = existingOpt.get();

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

            return articleRepository.save(existing);
        }

        // ---- Create new article ----
        Article newArticle = new Article();
        newArticle.setTitle(dto.getTitle());
        newArticle.setContent(dto.getAbstractText());

        newArticle.setCreatedAt(dto.get_ts() != null
                ? OffsetDateTime.ofInstant(Instant.ofEpochSecond(dto.get_ts()), ZoneOffset.UTC)
                : OffsetDateTime.now());

        // ---- Category mapping ----
        String scholarName = dto.getScholarName().toLowerCase();
        ArticleCategory category;
        if (scholarName.equals("project") || scholarName.equals("thesis")) {
            category = categoryCache.get("research");
            if (scholarName.equals("project")) {
                newArticle.setSourceUrl("https://scholar.itb.ac.id/" + "project_detail/" + dto.getUrl());
            } else {
                newArticle.setSourceUrl("https://scholar.itb.ac.id/" + "thesis_detail/" + dto.getUrl());
            }
        } else if (scholarName.equals("paper") || scholarName.equals("patent") || scholarName.equals("outreach")) {
            category = categoryCache.get("publication");
            if (scholarName.equals("paper")) {
                newArticle.setSourceUrl("https://scholar.itb.ac.id/" + "paper_detail/" + dto.getUrl());
            } else if (scholarName.equals("patent")) {
                newArticle.setSourceUrl("https://scholar.itb.ac.id/" + "patent_detail/" + dto.getUrl());
            } else {
                newArticle.setSourceUrl("https://scholar.itb.ac.id/" + "outreach_detail/" + dto.getUrl());
            }
        } else {
            category = categoryCache.get("general");
        }
        if (category == null) {
            throw new IllegalStateException("No matching category found for: " + dto.getScholarName());
        }
        newArticle.setArticleCategory(category);

        // ---- Scholar mapping ----
        Scholar scholar = scholarCache.get(dto.getScholarName().toLowerCase());
        if (scholar == null) {
            throw new IllegalArgumentException("Scholar not found: " + dto.getScholarName());
        }
        newArticle.setScholar(scholar);

        // Save first to get ID
        Article savedArticle = articleRepository.save(newArticle);

        // ---- Add goals ----
        if (dto.getSdg() != null && !dto.getSdg().isEmpty()) {
            dto.getSdg().stream()
                    .map(String::toLowerCase)
                    .distinct()
                    .forEach(sdgName -> {
                        Goal goal = goalCache.get(sdgName);
                        if (goal != null) {
                            savedArticle.addGoal(goal);
                        }
                    });
        }

        return articleRepository.save(savedArticle);
    }



}
