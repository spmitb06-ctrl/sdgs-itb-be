package com.sdgs.itb.infrastructure.news.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewsGoalStatsDTO {
    private Long goalId;
    private Integer goalNumber;
    private String title;
    private String color;
    private String icon;
    private Long count;
}
