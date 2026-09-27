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

    // Cache goals by their number (1 to 17)
    private final Map<Integer, Goal> goalByNumberCache = new HashMap<>();
    private final Map<String, Scholar> scholarCache = new HashMap<>();
    private final Map<String, NewsCategory> categoryCache = new HashMap<>();
    private final Map<Long, List<Unit>> unitCache = new HashMap<>();

    // Strict front match: "GOAL 17", "goal 17", "Goal17", "Goal 1:", etc.
    private static final Pattern FRONT_GOAL_PATTERN = Pattern.compile("(?i)^\\s*goal\\s*(\\d{1,2})");

    private Integer extractGoalNumber(String text) {
        if (text == null || text.trim().isEmpty()) return null;
        Matcher matcher = FRONT_GOAL_PATTERN.matcher(text.trim());
        if (matcher.find()) {
            try {
                int num = Integer.parseInt(matcher.group(1));
                if (num >= 1 && num <= 17) {
                    return num;
                }
            } catch (NumberFormatException ignored) {}
        }
        return null;
    }

    private void initCaches() {
        if (goalByNumberCache.isEmpty()) {
            List<Goal> allGoals = goalRepository.findAll();
            for (Goal g : allGoals) {
                Integer number = g.getGoalNumber();
                if (number == null || number < 1 || number > 17) {
                    number = extractGoalNumber(g.getTitle());
                }
                if (number != null && number >= 1 && number <= 17) {
                    goalByNumberCache.put(number, g);
                }
            }
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

    private Goal resolveGoal(String rawSdg) {
        Integer num = extractGoalNumber(rawSdg);
        if (num != null) {
            return goalByNumberCache.get(num);
        }
        return null;
    }

    @Override
    @Transactional
    public News importFromTypesense(TypesenseNewsExportDTO dto) {
        initCaches();

        String scholarName = dto.getScholarName().toLowerCase().trim();
        String cleanTitle = dto.getTitle() != null ? dto.getTitle().trim() : "";

        // 1. Resolve URLs
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

        // 2. Deduplication Check
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
                dto.getSdg().forEach(sdgStr -> {
                    Goal goal = resolveGoal(sdgStr);
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

        // 5. Build New Entity
        News newNews = new News();
        newNews.setTitle(cleanTitle);
        newNews.setContent(defaultContent);
        newNews.setSourceUrl(fullSourceUrl);
        newNews.setThumbnailUrl(thumbnailUrl);
        newNews.setScholarYear(dto.getYear());
        newNews.setEventDate(dto.getDateTime() != null ? dto.getDateTime() : LocalDate.now());
        newNews.setNewsCategory(category);
        newNews.setScholar(scholar);

        // Add goals using number extractor
        if (dto.getSdg() != null && !dto.getSdg().isEmpty()) {
            dto.getSdg().forEach(sdgStr -> {
                Goal goal = resolveGoal(sdgStr);
                if (goal != null) {
                    newNews.addGoal(goal);
                }
            });
        }

        // Add units
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
        initCaches();

        String scholarName = dto.getScholarName().toLowerCase().trim();
        String cleanTitle = dto.getTitle() != null ? dto.getTitle().trim() : "";

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
                dto.getSdg().forEach(sdgStr -> {
                    Goal goal = resolveGoal(sdgStr);
                    if (goal != null) {
                        existing.addGoal(goal);
                    }
                });
            }
            newsRepository.save(existing);
            return false;
        }

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
            dto.getSdg().forEach(sdgStr -> {
                Goal goal = resolveGoal(sdgStr);
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