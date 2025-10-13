package com.sdgs.itb.infrastructure.policy.dto;

import com.sdgs.itb.infrastructure.goal.dto.GoalDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PolicyGoalDTO {
    private Long id;
    private OffsetDateTime createdAt;
    private GoalDTO goals;
}
