package com.sdgs.itb.infrastructure.goal.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoalScholarRequest {
    private Long goalId;
    private Long scholarId;
    private String link;
}
