package com.sdgs.itb.infrastructure.news.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewsCategoryStatsDTO {
    private Long categoryId;
    private String category;
    private Long count;
}
