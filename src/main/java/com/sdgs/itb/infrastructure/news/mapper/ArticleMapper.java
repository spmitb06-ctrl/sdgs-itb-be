package com.sdgs.itb.infrastructure.news.mapper;

import com.sdgs.itb.entity.faculty.Faculty;
import com.sdgs.itb.entity.news.Article;
import com.sdgs.itb.entity.news.ArticleFaculty;
import com.sdgs.itb.entity.news.ArticleGoal;
import com.sdgs.itb.entity.news.ArticleImage;
import com.sdgs.itb.entity.sdgs.Goal;
import com.sdgs.itb.infrastructure.faculty.dto.FacultyDTO;
import com.sdgs.itb.infrastructure.news.dto.ArticleDTO;
import com.sdgs.itb.infrastructure.news.dto.ArticleFacultyDTO;
import com.sdgs.itb.infrastructure.news.dto.ArticleGoalDTO;
import com.sdgs.itb.infrastructure.sdgs.dto.GoalDTO;

import java.util.stream.Collectors;

public class ArticleMapper {
    public static Article toEntity(ArticleDTO dto) {
        Article article = new Article();
        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());
        article.setThumbnailUrl(dto.getThumbnailUrl());
        article.setSourceUrl(dto.getSourceUrl());

        if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
            article.setImages(dto.getImageUrls().stream()
                    .map(url -> ArticleImage.builder()
                            .imageUrl(url)
                            .article(article)
                            .build())
                    .collect(Collectors.toList()));
        }

        return article;
    }

    public static ArticleDTO toDTO(Article article) {
        ArticleDTO dto = new ArticleDTO();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setContent(article.getContent());
        dto.setThumbnailUrl(article.getThumbnailUrl());
        dto.setSourceUrl(article.getSourceUrl());
        dto.setCreatedAt(article.getCreatedAt());

        dto.setImageUrls(article.getImages().stream()
                .map(ArticleImage::getImageUrl)
                .collect(Collectors.toList()));

        dto.setArticleFaculties(article.getArticleFaculties().stream()
                .map(ArticleMapper::toArticleFacultyDTO)
                .collect(Collectors.toList()));

        dto.setArticleGoals(article.getArticleGoals().stream()
                .map(ArticleMapper::toArticleGoalDTO)
                .collect(Collectors.toList()));

        if (article.getArticleCategory() != null) {
            dto.setCategoryId(article.getArticleCategory().getId());
            dto.setCategoryName(article.getArticleCategory().getCategory());
        }

        if (article.getScholar() != null) {
            dto.setScholarId(article.getScholar().getId());
            dto.setScholarName(article.getScholar().getName());
        }

        return dto;
    }

    public static FacultyDTO toFacultyDTO(Faculty faculty) {
        FacultyDTO dto = new FacultyDTO();
        dto.setId(faculty.getId());
        dto.setName(faculty.getName());
        return dto;
    }

    public static GoalDTO toGoalDTO(Goal goal) {
        GoalDTO dto = new GoalDTO();
        dto.setId(goal.getId());
        dto.setTitle(goal.getTitle());
        dto.setDescription(goal.getDescription());
        dto.setColor(goal.getColor());
        dto.setIcon(goal.getIcon());
        dto.setLinkUrl(goal.getLinkUrl());
        return dto;
    }

    public static ArticleFacultyDTO toArticleFacultyDTO(ArticleFaculty articleFaculty) {
        ArticleFacultyDTO dto = new ArticleFacultyDTO();
        dto.setId(articleFaculty.getId());
        dto.setFaculties(toFacultyDTO(articleFaculty.getFaculty()));
        return dto;
    }

    public static ArticleGoalDTO toArticleGoalDTO(ArticleGoal articleGoal) {
        ArticleGoalDTO dto = new ArticleGoalDTO();
        dto.setId(articleGoal.getId());
        dto.setGoals(toGoalDTO(articleGoal.getGoal()));
        return dto;
    }
}

