package com.sdgs.itb.service.news.impl;

import com.sdgs.itb.common.exceptions.DataNotFoundException;
import com.sdgs.itb.entity.unit.Unit;
import com.sdgs.itb.entity.news.*;
import com.sdgs.itb.entity.goal.Goal;
import com.sdgs.itb.entity.goal.Scholar;
import com.sdgs.itb.infrastructure.news.repository.*;
import com.sdgs.itb.infrastructure.unit.repository.UnitRepository;
import com.sdgs.itb.infrastructure.news.dto.NewsDTO;
import com.sdgs.itb.infrastructure.news.dto.NewsUnitDTO;
import com.sdgs.itb.infrastructure.news.dto.NewsGoalDTO;
import com.sdgs.itb.infrastructure.news.mapper.NewsMapper;
import com.sdgs.itb.infrastructure.goal.repository.GoalRepository;
import com.sdgs.itb.infrastructure.goal.repository.ScholarRepository;
import com.sdgs.itb.service.news.NewsService;
import com.sdgs.itb.service.news.specification.NewsSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsRepository repository;
    private final NewsUnitRepository newsUnitRepository;
    private final NewsGoalRepository newsGoalRepository;
    private final NewsImageRepository newsImageRepository;
    private final NewsCategoryRepository categoryRepository;
    private final ScholarRepository scholarRepository;
    private final UnitRepository unitRepository;
    private final GoalRepository goalRepository;

    @Override
    public NewsDTO createNews(NewsDTO dto) {
//        String userRole = Claims.getRoleFromJwt();
//        if (userRole == null ||
//                !(userRole.equals(RoleType.ADMIN_WEB) || userRole.equals(RoleType.ADMIN_FACULTY))) {
//            throw new UnauthorizedException("Only Admin can create new News!");
//        }

        News news = NewsMapper.toEntity(dto);

        if (dto.getCategoryId() != null) {
            NewsCategory category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new DataNotFoundException("Category not found"));
            news.setNewsCategory(category);
        }

        if (dto.getScholarId() != null) {
            Scholar scholar = scholarRepository.findById(dto.getScholarId())
                    .orElseThrow(() -> new DataNotFoundException("Scholar not found"));
            news.setScholar(scholar);
        }

        // Save first to generate ID
        News savedNews = repository.saveAndFlush(news);

        // Units
        if (dto.getUnitIds() != null && !dto.getUnitIds().isEmpty()) {
            List<Unit> units = unitRepository.findAllById(dto.getUnitIds());
            if (units.isEmpty()) throw new DataNotFoundException("No valid units found");

            for (Unit unit : units) {  // Regular for-loop
                NewsUnit af = NewsUnit.builder()
                        .news(savedNews)
                        .unit(unit)
                        .build();
                savedNews.getNewsUnits().add(af);
            }
        }

        // Goals
        if (dto.getGoalIds() != null && !dto.getGoalIds().isEmpty()) {
            List<Goal> goals = goalRepository.findAllById(dto.getGoalIds());
            if (goals.isEmpty()) throw new DataNotFoundException("No valid goals found");

            for (Goal goal : goals) { // Regular for-loop
                NewsGoal ag = NewsGoal.builder()
                        .news(savedNews)
                        .goal(goal)
                        .build();
                savedNews.getNewsGoals().add(ag);
            }
        }

        // Images
        if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
            for (String url : dto.getImageUrls()) {
                NewsImage img = NewsImage.builder()
                        .news(savedNews)
                        .imageUrl(url)
                        .build();
                savedNews.getImages().add(img);
            }
        }

        savedNews = repository.save(savedNews);
        return NewsMapper.toDTO(savedNews);
    }

    @Override
    @Transactional
    public NewsDTO updateNews(Long id, NewsDTO dto) {
//        String userRole = Claims.getRoleFromJwt();
//        if (userRole == null ||
//                !(userRole.equals(RoleType.ADMIN_WEB) || userRole.equals(RoleType.ADMIN_FACULTY))) {
//            throw new UnauthorizedException("Only Admin can edit News!");
//        }

        News existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found"));

        existing.setTitle(dto.getTitle());
        existing.setContent(dto.getContent());
        existing.setThumbnailUrl(dto.getThumbnailUrl());
        existing.setSourceUrl(dto.getSourceUrl());
        existing.setEventDate(dto.getEventDate());

        // Category
        if (dto.getCategoryId() != null) {
            NewsCategory category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new DataNotFoundException("Category not found"));
            existing.setNewsCategory(category);
        }

        // Scholar
        if (dto.getScholarId() != null) {
            Scholar scholar = scholarRepository.findById(dto.getScholarId())
                    .orElseThrow(() -> new DataNotFoundException("Scholar not found"));
            existing.setScholar(scholar);
        }

        // Units
        if (dto.getUnitIds() != null && !dto.getUnitIds().isEmpty()) {
            List<Unit> newUnits = unitRepository.findAllById(dto.getUnitIds());
            if (newUnits.isEmpty()) throw new DataNotFoundException("No valid units found");

            // Fetch all current active NewsUnit relations for this News
            List<NewsUnit> existingNewsUnits = newsUnitRepository.findAllByNewsId(existing.getId());

            // Hard delete NewsUnit records that are not in dto.getUnitIds()
            for (NewsUnit oldNewsUnit : existingNewsUnits) {
                if (!dto.getUnitIds().contains(oldNewsUnit.getUnit().getId())) {
                    newsUnitRepository.delete(oldNewsUnit);
                }
            }

            // Remove units that already exist (to avoid duplicates)
            newUnits.removeIf(unit ->
                    newsUnitRepository.findByNewsIdAndUnitId(existing.getId(), unit.getId()).isPresent()
            );

            // Add new NewsUnit entries for remaining units
            for (Unit unit : newUnits) {
                NewsUnit newNewsUnit = NewsUnit.builder()
                        .news(existing)
                        .unit(unit)
                        .build();
                existing.getNewsUnits().add(newNewsUnit);
            }
        }

        // Goals
        if (dto.getGoalIds() != null && !dto.getGoalIds().isEmpty()) {
            List<Goal> newGoals = goalRepository.findAllById(dto.getGoalIds());
            if (newGoals.isEmpty()) throw new DataNotFoundException("No valid goals found");

            // Fetch all current active NewsGoal relations for this News
            List<NewsGoal> existingNewsGoals = newsGoalRepository.findAllByNewsId(existing.getId());

            // Hard delete NewsGoal records that are not in dto.getGoalIds()
            for (NewsGoal oldNewsGoal : existingNewsGoals) {
                if (!dto.getGoalIds().contains(oldNewsGoal.getGoal().getId())) {
                    newsGoalRepository.delete(oldNewsGoal);
                }
            }

            // Remove goals that already exist (to avoid duplicates)
            newGoals.removeIf(goal ->
                    newsGoalRepository.findByNewsIdAndGoalId(existing.getId(), goal.getId()).isPresent()
            );

            // Add new NewsGoal entries for remaining goals
            for (Goal goal : newGoals) {
                NewsGoal newNewsGoal = NewsGoal.builder()
                        .news(existing)
                        .goal(goal)
                        .build();
                existing.getNewsGoals().add(newNewsGoal);
            }
        }

        // Images
        if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
            for (String url : dto.getImageUrls()) {
                NewsImage image = NewsImage.builder()
                        .news(existing)
                        .imageUrl(url)
                        .build();
                existing.getImages().add(image);
            }
        }

        News saved = repository.save(existing);
        return NewsMapper.toDTO(saved);

//        return NewsMapper.toDTO(repository.save(existing));
    }

    @Override
    public void deleteNews(Long id) {
//        String userRole = Claims.getRoleFromJwt();
//        if (userRole == null ||
//                !(userRole.equals(RoleType.ADMIN_WEB) || userRole.equals(RoleType.ADMIN_FACULTY))) {
//            throw new UnauthorizedException("Only Admin can delete News!");
//        }

        News news = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found"));

        // Soft delete related images
        if (news.getImages() != null && !news.getImages().isEmpty()) {
            news.getImages().forEach(image -> {
                image.setImageUrl(image.getImageUrl()); // keep reference
                image.setNews(news);
                image.setDeletedAt(java.time.OffsetDateTime.now()); // add deletedAt field to NewsImage
            });
        }

        // Soft delete related units
        if (news.getNewsUnits() != null && !news.getNewsUnits().isEmpty()) {
            news.getNewsUnits().forEach(af -> af.setDeletedAt(java.time.OffsetDateTime.now()));
        }

        // Soft delete the news itself
        news.setDeletedAt(java.time.OffsetDateTime.now());

        repository.save(news);
    }


    @Override
    public NewsDTO getNews(Long id) {
        return repository.findById(id)
                .filter(news -> news.getDeletedAt() == null)
                .map(NewsMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("News not found"));
    }

    @Override
    public Page<NewsDTO> getNews(
            String title,
            List<Long> goalIds,
//            Long categoryId,
            List<Long> categoryIds,
            List<Long> scholarIds,
            List<Long> unitIds,
            String year,
            String sortBy,
            String sortDir,
            int page,
            int size
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<News> spec = Specification
                .where(NewsSpecification.notDeleted())
                .and(NewsSpecification.hasTitle(title))
                .and(NewsSpecification.hasGoal(goalIds))
//                .and(NewsSpecification.hasCategory(categoryId))
                .and(NewsSpecification.hasCategories(categoryIds))
                .and(NewsSpecification.hasScholars(scholarIds))
                .and(NewsSpecification.hasUnit(unitIds))
                .and(NewsSpecification.hasYear(year));

        return repository.findAll(spec, pageable).map(NewsMapper::toDTO);
    }

}
