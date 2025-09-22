package com.sdgs.itb.infrastructure.news.dto;

import com.sdgs.itb.infrastructure.sdgs.dto.GoalDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleGoalDTO {
    private Long id;
    private OffsetDateTime createdAt;
    private GoalDTO goals;
}
