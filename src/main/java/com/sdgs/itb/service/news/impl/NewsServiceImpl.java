package com.sdgs.itb.service.news.impl;

import com.sdgs.itb.common.exceptions.DataNotFoundException;
import com.sdgs.itb.entity.unit.Unit;
import com.sdgs.itb.entity.news.*;
import com.sdgs.itb.entity.goal.Goal;
import com.sdgs.itb.entity.goal.Scholar;
import com.sdgs.itb.infrastructure.news.repository.*;
import com.sdgs.itb.infrastructure.unit.repository.UnitRepository;
import com.sdgs.itb.infrastructure.news.dto.NewsDTO;
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

import java.time.LocalDate;
import java.util.List;

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

        News savedNews = repository.saveAndFlush(news);

        if (dto.getUnitIds() != null && !dto.getUnitIds().isEmpty()) {
            List<Unit> units = unitRepository.findAllById(dto.getUnitIds());
            if (units.isEmpty()) throw new DataNotFoundException("No valid units found");

            for (Unit unit : units) {
                NewsUnit af = NewsUnit.builder()
                        .news(savedNews)
                        .unit(unit)
                        .build();
                savedNews.getNewsUnits().add(af);
            }
        }

        if (dto.getGoalIds() != null && !dto.getGoalIds().isEmpty()) {
            List<Goal> goals = goalRepository.findAllById(dto.getGoalIds());
            if (goals.isEmpty()) throw new DataNotFoundException("No valid goals found");

            for (Goal goal : goals) {
                NewsGoal ag = NewsGoal.builder()
                        .news(savedNews)
                        .goal(goal)
                        .build();
                savedNews.getNewsGoals().add(ag);
            }
        }

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
        News existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found"));

        existing.setTitle(dto.getTitle());
        existing.setContent(dto.getContent());
        existing.setThumbnailUrl(dto.getThumbnailUrl());
        existing.setSourceUrl(dto.getSourceUrl());
        existing.setEventDate(dto.getEventDate());

        if (dto.getCategoryId() != null) {
            NewsCategory category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new DataNotFoundException("Category not found"));
            existing.setNewsCategory(category);
        }

        if (dto.getScholarId() != null) {
            Scholar scholar = scholarRepository.findById(dto.getScholarId())
                    .orElseThrow(() -> new DataNotFoundException("Scholar not found"));
            existing.setScholar(scholar);
            existing.setScholarYear(dto.getScholarYear());
        }

        if (dto.getUnitIds() != null && !dto.getUnitIds().isEmpty()) {
            List<Unit> newUnits = unitRepository.findAllById(dto.getUnitIds());
            if (newUnits.isEmpty()) throw new DataNotFoundException("No valid units found");

            List<NewsUnit> existingNewsUnits = newsUnitRepository.findAllByNewsId(existing.getId());
            for (NewsUnit oldNewsUnit : existingNewsUnits) {
                if (!dto.getUnitIds().contains(oldNewsUnit.getUnit().getId())) {
                    newsUnitRepository.delete(oldNewsUnit);
                }
            }

            newUnits.removeIf(unit ->
                    newsUnitRepository.findByNewsIdAndUnitId(existing.getId(), unit.getId()).isPresent()
            );

            for (Unit unit : newUnits) {
                NewsUnit newNewsUnit = NewsUnit.builder()
                        .news(existing)
                        .unit(unit)
                        .build();
                existing.getNewsUnits().add(newNewsUnit);
            }
        }

        if (dto.getGoalIds() != null && !dto.getGoalIds().isEmpty()) {
            List<Goal> newGoals = goalRepository.findAllById(dto.getGoalIds());
            if (newGoals.isEmpty()) throw new DataNotFoundException("No valid goals found");

            List<NewsGoal> existingNewsGoals = newsGoalRepository.findAllByNewsId(existing.getId());
            for (NewsGoal oldNewsGoal : existingNewsGoals) {
                if (!dto.getGoalIds().contains(oldNewsGoal.getGoal().getId())) {
                    newsGoalRepository.delete(oldNewsGoal);
                }
            }

            newGoals.removeIf(goal ->
                    newsGoalRepository.findByNewsIdAndGoalId(existing.getId(), goal.getId()).isPresent()
            );

            for (Goal goal : newGoals) {
                NewsGoal newNewsGoal = NewsGoal.builder()
                        .news(existing)
                        .goal(goal)
                        .build();
                existing.getNewsGoals().add(newNewsGoal);
            }
        }

        if (dto.getImageUrls() != null) {
            List<NewsImage> existingImages = newsImageRepository.findAllByNewsId(existing.getId());
            for (NewsImage oldImage : existingImages) {
                if (!dto.getImageUrls().contains(oldImage.getImageUrl())) {
                    newsImageRepository.delete(oldImage);
                }
            }

            List<String> existingUrls = existingImages.stream()
                    .map(NewsImage::getImageUrl)
                    .toList();

            for (String url : dto.getImageUrls()) {
                if (!existingUrls.contains(url)) {
                    NewsImage image = NewsImage.builder()
                            .news(existing)
                            .imageUrl(url)
                            .build();
                    existing.getImages().add(image);
                }
            }
        }

        News saved = repository.save(existing);
        return NewsMapper.toDTO(saved);
    }

    @Override
    public void deleteNews(Long id) {
        News news = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found"));

        if (news.getImages() != null && !news.getImages().isEmpty()) {
            news.getImages().forEach(image -> {
                image.setImageUrl(image.getImageUrl());
                image.setNews(news);
                image.setDeletedAt(java.time.OffsetDateTime.now());
            });
        }

        if (news.getNewsUnits() != null && !news.getNewsUnits().isEmpty()) {
            news.getNewsUnits().forEach(af -> af.setDeletedAt(java.time.OffsetDateTime.now()));
        }

        news.setDeletedAt(java.time.OffsetDateTime.now());
        repository.save(news);
    }

    @Override
    public void deleteNewsImage(Long imageId) {
        NewsImage newsImage = newsImageRepository.findById(imageId)
                .orElseThrow(() -> new DataNotFoundException("Image not found"));
        newsImageRepository.delete(newsImage);
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
            List<Long> categoryIds,
            List<Long> scholarIds,
            List<Long> unitIds,
            String year,
            LocalDate startDate,
            LocalDate endDate,
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
                .and(NewsSpecification.hasCategories(categoryIds))
                .and(NewsSpecification.hasScholars(scholarIds))
                .and(NewsSpecification.hasUnit(unitIds));

        // Prioritaskan filter rentang tanggal. Jika rentang tanggal tidak ada, gunakan filter tahun.
        if (startDate != null || endDate != null) {
            spec = spec.and(NewsSpecification.eventDateBetween(startDate, endDate));
        } else if (year != null && !year.trim().isEmpty()) {
            spec = spec.and(NewsSpecification.hasYear(year));
        }

        return repository.findAll(spec, pageable).map(NewsMapper::toDTO);
    }
}