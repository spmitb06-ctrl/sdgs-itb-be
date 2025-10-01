package com.sdgs.itb.infrastructure.goal.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoalDTO {
    private Long id;
    private Integer goalNumber;
    private String title;
    private String description;
    private String color;
    private String icon;
    private String editedIcon;
    private String linkUrl;

    private List<GoalScholarDTO> scholars;
}


