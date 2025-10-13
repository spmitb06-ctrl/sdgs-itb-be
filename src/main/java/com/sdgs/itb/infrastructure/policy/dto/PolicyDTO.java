package com.sdgs.itb.infrastructure.policy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PolicyDTO {
    private Long id;
    private String title;
    private String description;
    private String fileUrl;
    private String year;
    private String imageUrl;
    private Long categoryId;
    private String categoryName;
    private String categoryColor;
    private List<Long> goalIds;

    private List<PolicyGoalDTO> policyGoals;
}
