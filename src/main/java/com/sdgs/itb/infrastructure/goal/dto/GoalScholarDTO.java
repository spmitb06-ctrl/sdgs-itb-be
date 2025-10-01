package com.sdgs.itb.infrastructure.goal.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoalScholarDTO {
    private Long scholarId;
    private String scholarName;
    private String link;
    private Integer count;
}
