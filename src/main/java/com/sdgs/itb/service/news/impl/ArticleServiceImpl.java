package com.sdgs.itb.service.news.impl;

import com.sdgs.itb.common.exceptions.DataNotFoundException;
import com.sdgs.itb.entity.faculty.Faculty;
import com.sdgs.itb.entity.news.*;
import com.sdgs.itb.entity.sdgs.Goal;
import com.sdgs.itb.entity.sdgs.Scholar;
import com.sdgs.itb.infrastructure.faculty.repository.FacultyRepository;
import com.sdgs.itb.infrastructure.news.dto.ArticleDTO;
import com.sdgs.itb.infrastructure.news.mapper.ArticleMapper;
import com.sdgs.itb.infrastructure.news.repository.ArticleCategoryRepository;
import com.sdgs.itb.infrastructure.news.repository.ArticleImageRepository;
import com.sdgs.itb.infrastructure.news.repository.ArticleRepository;
import com.sdgs.itb.infrastructure.sdgs.repository.GoalRepository;
import com.sdgs.itb.infrastructure.sdgs.repository.ScholarRepository;
import com.sdgs.itb.service.news.ArticleService;
import com.sdgs.itb.service.news.specification.ArticleSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository repository;
    private final ArticleImageRepository articleImageRepository;
    private final ArticleCategoryRepository categoryRepository;
    private final ScholarRepository scholarRepository;
    private final FacultyRepository facultyRepository;
    private final GoalRepository goalRepository;

    @Override
    public ArticleDTO createArticle(ArticleDTO dto) {
//        String userRole = Claims.getRoleFromJwt();
//        if (userRole == null ||
//                !(userRole.equals(RoleType.ADMIN_WEB) || userRole.equals(RoleType.ADMIN_FACULTY))) {
//            throw new UnauthorizedException("Only Admin can create new Article!");
//        }

        Article article = ArticleMapper.toEntity(dto);

        if (dto.getCategoryId() != null) {
            ArticleCategory category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new DataNotFoundException("Category not found"));
            article.setArticleCategory(category);
        }

        if (dto.getScholarId() != null) {
            Scholar scholar = scholarRepository.findById(dto.getScholarId())
                    .orElseThrow(() -> new DataNotFoundException("Scholar Category not found"));
            article.setScholar(scholar);
        }

        repository.saveAndFlush(article);

        List<Faculty> faculties = facultyRepository.findAllById(dto.getFacultyIds());
        if (faculties.isEmpty()) {
            throw new DataNotFoundException("No valid faculties found");
        }

        faculties.forEach(faculty -> faculty.addArticle(article));

        List<Goal> goals = goalRepository.findAllById(dto.getGoalIds());
        if (goals.isEmpty()) {
            throw new DataNotFoundException("No valid goals found");
        }

        goals.forEach(goal -> goal.addArticle(article));

        Article savedArticle = repository.save(article);

        // Save related Cloudinary images if provided
        if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
            for (String url : dto.getImageUrls()) {
                ArticleImage image = ArticleImage.builder()
                        .article(savedArticle)
                        .imageUrl(url)
                        .build();
                articleImageRepository.save(image);
                savedArticle.getImages().add(image);
            }
        }

        return ArticleMapper.toDTO(repository.save(article));
    }

    @Override
    public ArticleDTO updateArticle(Long id, ArticleDTO dto) {
//        String userRole = Claims.getRoleFromJwt();
//        if (userRole == null ||
//                !(userRole.equals(RoleType.ADMIN_WEB) || userRole.equals(RoleType.ADMIN_FACULTY))) {
//            throw new UnauthorizedException("Only Admin can edit Article!");
//        }

        Article existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        existing.setTitle(dto.getTitle());
        existing.setContent(dto.getContent());
        existing.setThumbnailUrl(dto.getThumbnailUrl());

        if (dto.getCategoryId() != null) {
            ArticleCategory category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new DataNotFoundException("Category not found"));
            existing.setArticleCategory(category);
        }

        if (dto.getScholarId() != null) {
            Scholar scholar = scholarRepository.findById(dto.getScholarId())
                    .orElseThrow(() -> new DataNotFoundException("Scholar Category not found"));
            existing.setScholar(scholar);
        }

        // Update faculties
        existing.getArticleFaculties().clear();
        if (dto.getArticleFaculties() != null) {
            dto.getArticleFaculties().forEach(afDto -> {
                Faculty faculty = facultyRepository.findById(afDto.getFaculties().getId())
                        .orElseThrow(() -> new RuntimeException("Faculty not found"));
                ArticleFaculty articleFaculty = ArticleFaculty.builder()
                        .article(existing)
                        .faculty(faculty)
                        .build();
                existing.getArticleFaculties().add(articleFaculty);
            });
        }

        // Update goals
        existing.getArticleGoals().clear();
        if (dto.getArticleGoals() != null) {
            dto.getArticleGoals().forEach(agDto -> {
                Goal goal = goalRepository.findById(agDto.getGoals().getId())
                        .orElseThrow(() -> new RuntimeException("Goal not found"));
                ArticleGoal articleGoal = ArticleGoal.builder()
                        .article(existing)
                        .goal(goal)
                        .build();
                existing.getArticleGoals().add(articleGoal);
            });
        }

        // Update images
        existing.getImages().clear();
        if (dto.getImageUrls() != null) {
            dto.getImageUrls().forEach(url ->
                    existing.getImages().add(
                            com.sdgs.itb.entity.news.ArticleImage.builder()
                                    .imageUrl(url)
                                    .article(existing)
                                    .build()
                    )
            );
        }

        return ArticleMapper.toDTO(repository.save(existing));
    }

    @Override
    public void deleteArticle(Long id) {
//        String userRole = Claims.getRoleFromJwt();
//        if (userRole == null ||
//                !(userRole.equals(RoleType.ADMIN_WEB) || userRole.equals(RoleType.ADMIN_FACULTY))) {
//            throw new UnauthorizedException("Only Admin can delete Article!");
//        }

        Article article = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        // Soft delete related images
        if (article.getImages() != null && !article.getImages().isEmpty()) {
            article.getImages().forEach(image -> {
                image.setImageUrl(image.getImageUrl()); // keep reference
                image.setArticle(article);
                image.setDeletedAt(java.time.OffsetDateTime.now()); // add deletedAt field to ArticleImage
            });
        }

        // Soft delete related faculties
        if (article.getArticleFaculties() != null && !article.getArticleFaculties().isEmpty()) {
            article.getArticleFaculties().forEach(af -> af.setDeletedAt(java.time.OffsetDateTime.now()));
        }

        // Soft delete the article itself
        article.setDeletedAt(java.time.OffsetDateTime.now());

        repository.save(article);
    }


    @Override
    public ArticleDTO getArticle(Long id) {
        return repository.findById(id)
                .filter(article -> article.getDeletedAt() == null)
                .map(ArticleMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Article not found"));
    }

    @Override
    public Page<ArticleDTO> getArticles(
            String title,
            List<Long> goalIds,
            List<Long> categoryIds,
            List<Long> scholarIds,
            List<Long> facultyIds,
            String year,
            String sortBy,
            String sortDir,
            int page,
            int size
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Article> spec = Specification
                .where(ArticleSpecification.notDeleted())
                .and(ArticleSpecification.hasTitle(title))
                .and(ArticleSpecification.hasGoal(goalIds))
                .and(ArticleSpecification.hasCategories(categoryIds))
                .and(ArticleSpecification.hasScholars(scholarIds))
                .and(ArticleSpecification.hasFaculty(facultyIds))
                .and(ArticleSpecification.hasYear(year));

        return repository.findAll(spec, pageable).map(ArticleMapper::toDTO);
    }

}
