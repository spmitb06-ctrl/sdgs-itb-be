package com.sdgs.itb.infrastructure.news.dto;

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
public class NewsGoalDTO {
    private Long id;
    private OffsetDateTime createdAt;
    private GoalDTO goals;
}
