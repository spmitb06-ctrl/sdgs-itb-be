package com.sdgs.itb.infrastructure.sdgs.dto;

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
