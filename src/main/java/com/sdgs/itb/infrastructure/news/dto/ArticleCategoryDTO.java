package com.sdgs.itb.infrastructure.news.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleCategoryDTO {
    private Long id;
    private String category;
}

