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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class NewsImportServiceImpl implements NewsImportService {

    private final ScholarRepository scholarRepository;
    private final GoalRepository goalRepository;
    private final NewsRepository newsRepository;
    private final NewsCategoryRepository newsCategoryRepository;
    private final UnitRepository unitRepository;

    // Map by goalNumber (1 - 17) instead of full title string
    private final Map<Integer, Goal> goalCache = new HashMap<>();
    private final Map<String, Scholar> scholarCache = new HashMap<>();
    private final Map<String, NewsCategory> categoryCache = new HashMap<>();
    private final Map<Long, List<Unit>> unitCache = new HashMap<>();

    // Regex to match "goal 1", "GOAL 17", "Goal 3:", "goal: 5", etc.
    private static final Pattern GOAL_NUMBER_PATTERN = Pattern.compile("(?i)^goal\\s*:?\\s*(\\d{1,2})");

    /**
     * Resolves a Goal entity by matching the leading "GOAL [1-17]" pattern
     */
    private Goal resolveGoalFromSdgString(String rawSdg) {
        if (rawSdg == null) return null;
        Matcher matcher = GOAL_NUMBER_PATTERN.matcher(rawSdg.trim());
        if (matcher.find()) {
            try {
                int goalNumber = Integer.parseInt(matcher.group(1));
                if (goalNumber >= 1 && goalNumber <= 17) {
                    return goalCache.get(goalNumber);
                }
            } catch (NumberFormatException ignored) {}
        }
        return null;
    }

    private void initializeCaches() {
        if (goalCache.isEmpty()) {
            goalRepository.findAll().forEach(goal ->
                    goalCache.put(goal.getGoalNumber(), goal)
            );
        }
        if (scholarCache.isEmpty()) {
            scholarRepository.findAll().forEach(scholar ->
                    scholarCache.put(scholar.getName().toLowerCase().trim(), scholar)
            );
        }
        if (categoryCache.isEmpty()) {
            newsCategoryRepository.findAll().forEach(category ->
                    categoryCache.put(category.getCategory().toLowerCase().trim(), category)
            );
        }
        if (unitCache.isEmpty()) {
            for (long i = 1; i <= 12; i++) {
                unitCache.put(i, unitRepository.findByOrganizationId(i));
            }
        }
    }

    @Override
    @Transactional
    public News importFromTypesense(TypesenseNewsExportDTO dto) {
        initializeCaches();

        String scholarName = dto.getScholarName().toLowerCase().trim();
        String cleanTitle = dto.getTitle() != null ? dto.getTitle().trim() : "";

        // 1. Resolve full sourceUrl & thumbnailUrl FIRST
        String fullSourceUrl;
        String thumbnailUrl;
        String defaultContent = dto.getAbstractText();

        if ("outreach".equals(scholarName)) {
            fullSourceUrl = "https://scholar.itb.ac.id/outreach_detail/" + dto.getUrl();
            thumbnailUrl = "/news/outreach.jpg";
        } else if ("project".equals(scholarName) && "pengabdian".equalsIgnoreCase(dto.getType())) {
            fullSourceUrl = "https://scholar.itb.ac.id/project_detail/" + dto.getUrl();
            thumbnailUrl = "/news/community-service.jpg";
        } else {
            switch (scholarName) {
                case "project" -> {
                    fullSourceUrl = "https://scholar.itb.ac.id/project_detail/" + dto.getUrl();
                    thumbnailUrl = "/news/project.jpg";
                }
                case "paper" -> {
                    fullSourceUrl = "https://scholar.itb.ac.id/paper_detail/" + dto.getUrl();
                    thumbnailUrl = "/news/paper.jpeg";
                }
                case "patent" -> {
                    fullSourceUrl = "https://scholar.itb.ac.id/paten_detail/" + dto.getUrl();
                    thumbnailUrl = "/news/patent.jpg";
                }
                case "thesis" -> {
                    fullSourceUrl = "https://scholar.itb.ac.id/thesis_detail/" + dto.getUrl();
                    thumbnailUrl = "/news/thesis.jpeg";
                }
                default -> {
                    fullSourceUrl = "https://scholar.itb.ac.id/" + scholarName + "_detail/" + dto.getUrl();
                    thumbnailUrl = "/news/default.jpg";
                }
            }
        }

        // 2. DEDUPLICATION: Check by fullSourceUrl OR by clean title
        Optional<News> existingOpt = newsRepository.findBySourceUrl(fullSourceUrl);

        if (existingOpt.isEmpty() && !cleanTitle.isEmpty()) {
            List<News> matches = newsRepository.findByTitleIgnoreCase(cleanTitle);
            if (!matches.isEmpty()) {
                existingOpt = Optional.of(matches.get(0));
            }
        }

        if (existingOpt.isPresent()) {
            News existing = existingOpt.get();

            // Add any missing goals using flexible number matching
            if (dto.getSdg() != null && !dto.getSdg().isEmpty()) {
                dto.getSdg().stream()
                        .distinct()
                        .forEach(sdgStr -> {
                            Goal goal = resolveGoalFromSdgString(sdgStr);
                            if (goal != null) {
                                existing.addGoal(goal);
                            }
                        });
            }

            return newsRepository.save(existing);
        }

        // 3. Resolve Category
        NewsCategory category;
        if ("outreach".equals(scholarName) || ("project".equals(scholarName) && "pengabdian".equalsIgnoreCase(dto.getType()))) {
            category = categoryCache.get("community service");
        } else {
            category = categoryCache.get("research & publication");
            if (category == null) {
                category = categoryCache.get("publication, research & paper");
            }
        }

        if (category == null) {
            throw new IllegalStateException("No matching category found for: " + scholarName
                    + ". Available: " + categoryCache.keySet());
        }

        // 4. Resolve Scholar
        Scholar scholar = scholarCache.get(scholarName);
        if (scholar == null) {
            throw new IllegalArgumentException("Scholar not found: " + scholarName);
        }

        // 5. Build New News Entity
        News newNews = new News();
        newNews.setTitle(cleanTitle);
        newNews.setContent(defaultContent);
        newNews.setSourceUrl(fullSourceUrl);
        newNews.setThumbnailUrl(thumbnailUrl);
        newNews.setScholarYear(dto.getYear());
        newNews.setEventDate(dto.getDateTime() != null ? dto.getDateTime() : LocalDate.now());
        newNews.setNewsCategory(category);
        newNews.setScholar(scholar);

        // Add goals before saving using flexible number matching
        if (dto.getSdg() != null && !dto.getSdg().isEmpty()) {
            dto.getSdg().stream()
                    .distinct()
                    .forEach(sdgStr -> {
                        Goal goal = resolveGoalFromSdgString(sdgStr);
                        if (goal != null) {
                            newNews.addGoal(goal);
                        }
                    });
        }

        // Add units before saving
        if (dto.getOrganizations() != null && !dto.getOrganizations().isEmpty()) {
            for (Long orgId : dto.getOrganizations()) {
                List<Unit> units = unitCache.getOrDefault(orgId, Collections.emptyList());
                for (Unit unit : units) {
                    newNews.addUnit(unit);
                }
            }
        }

        return newsRepository.save(newNews);
    }

    @Override
    @Transactional
    public boolean importOrUpdateFromTypesense(TypesenseNewsExportDTO dto) {
        initializeCaches();

        String scholarName = dto.getScholarName().toLowerCase().trim();
        String cleanTitle = dto.getTitle() != null ? dto.getTitle().trim() : "";

        // 1. Resolve full sourceUrl & thumbnailUrl
        String fullSourceUrl;
        String thumbnailUrl;
        String defaultContent = dto.getAbstractText();

        if ("outreach".equals(scholarName)) {
            fullSourceUrl = "https://scholar.itb.ac.id/outreach_detail/" + dto.getUrl();
            thumbnailUrl = "/news/outreach.jpg";
        } else if ("project".equals(scholarName) && "pengabdian".equalsIgnoreCase(dto.getType())) {
            fullSourceUrl = "https://scholar.itb.ac.id/project_detail/" + dto.getUrl();
            thumbnailUrl = "/news/community-service.jpg";
        } else {
            switch (scholarName) {
                case "project" -> {
                    fullSourceUrl = "https://scholar.itb.ac.id/project_detail/" + dto.getUrl();
                    thumbnailUrl = "/news/project.jpg";
                }
                case "paper" -> {
                    fullSourceUrl = "https://scholar.itb.ac.id/paper_detail/" + dto.getUrl();
                    thumbnailUrl = "/news/paper.jpeg";
                }
                case "patent" -> {
                    fullSourceUrl = "https://scholar.itb.ac.id/paten_detail/" + dto.getUrl();
                    thumbnailUrl = "/news/patent.jpg";
                }
                case "thesis" -> {
                    fullSourceUrl = "https://scholar.itb.ac.id/thesis_detail/" + dto.getUrl();
                    thumbnailUrl = "/news/thesis.jpeg";
                }
                default -> {
                    fullSourceUrl = "https://scholar.itb.ac.id/" + scholarName + "_detail/" + dto.getUrl();
                    thumbnailUrl = "/news/default.jpg";
                }
            }
        }

        // 2. Deduplication check
        Optional<News> existingOpt = newsRepository.findBySourceUrl(fullSourceUrl);
        if (existingOpt.isEmpty() && !cleanTitle.isEmpty()) {
            List<News> matches = newsRepository.findByTitleIgnoreCase(cleanTitle);
            if (!matches.isEmpty()) {
                existingOpt = Optional.of(matches.get(0));
            }
        }

        if (existingOpt.isPresent()) {
            News existing = existingOpt.get();
            if (dto.getSdg() != null && !dto.getSdg().isEmpty()) {
                dto.getSdg().stream()
                        .distinct()
                        .forEach(sdgStr -> {
                            Goal goal = resolveGoalFromSdgString(sdgStr);
                            if (goal != null) {
                                existing.addGoal(goal);
                            }
                        });
            }
            newsRepository.save(existing);
            return false;
        }

        // 3. Resolve Category & Scholar
        NewsCategory category;
        if ("outreach".equals(scholarName) || ("project".equals(scholarName) && "pengabdian".equalsIgnoreCase(dto.getType()))) {
            category = categoryCache.get("community service");
        } else {
            category = categoryCache.get("research & publication");
            if (category == null) {
                category = categoryCache.get("publication, research & paper");
            }
        }

        if (category == null) {
            throw new IllegalStateException("No matching category found for: " + scholarName);
        }

        Scholar scholar = scholarCache.get(scholarName);
        if (scholar == null) {
            throw new IllegalArgumentException("Scholar not found: " + scholarName);
        }

        // 4. Build New Entity
        News newNews = new News();
        newNews.setTitle(cleanTitle);
        newNews.setContent(defaultContent);
        newNews.setSourceUrl(fullSourceUrl);
        newNews.setThumbnailUrl(thumbnailUrl);
        newNews.setScholarYear(dto.getYear());
        newNews.setEventDate(dto.getDateTime() != null ? dto.getDateTime() : LocalDate.now());
        newNews.setNewsCategory(category);
        newNews.setScholar(scholar);

        if (dto.getSdg() != null && !dto.getSdg().isEmpty()) {
            dto.getSdg().stream()
                    .distinct()
                    .forEach(sdgStr -> {
                        Goal goal = resolveGoalFromSdgString(sdgStr);
                        if (goal != null) {
                            newNews.addGoal(goal);
                        }
                    });
        }

        if (dto.getOrganizations() != null && !dto.getOrganizations().isEmpty()) {
            for (Long orgId : dto.getOrganizations()) {
                List<Unit> units = unitCache.getOrDefault(orgId, Collections.emptyList());
                for (Unit unit : units) {
                    newNews.addUnit(unit);
                }
            }
        }

        newsRepository.save(newNews);
        return true;
    }
}