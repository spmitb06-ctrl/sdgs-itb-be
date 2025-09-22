package com.sdgs.itb.infrastructure.sdgs.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoalDTO {
    private Long id;
    private String title;
    private String description;
    private String color;
    private String icon;
    private String linkUrl;

    private List<GoalScholarDTO> scholars;
}


