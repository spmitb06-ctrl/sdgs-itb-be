package com.sdgs.itb.infrastructure.news.mapper;

import com.sdgs.itb.entity.faculty.Faculty;
import com.sdgs.itb.entity.news.ArticleCategory;
import com.sdgs.itb.infrastructure.faculty.dto.FacultyDTO;
import com.sdgs.itb.infrastructure.news.dto.ArticleCategoryDTO;

public class ArticleCategoryMapper {

    public static ArticleCategoryDTO toDTO(ArticleCategory articleCategory) {
        return ArticleCategoryDTO.builder()
                .id(articleCategory.getId())
                .category(articleCategory.getCategory())
                .build();
    }
}


