package com.sdgs.itb.infrastructure.news.mapper;

import com.sdgs.itb.entity.news.NewsCategory;
import com.sdgs.itb.infrastructure.news.dto.NewsCategoryDTO;

public class NewsCategoryMapper {

    public static NewsCategoryDTO toDTO(NewsCategory newsCategory) {
        return NewsCategoryDTO.builder()
                .id(newsCategory.getId())
                .category(newsCategory.getCategory())
                .build();
    }
}


