package com.sdgs.itb.infrastructure.policy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PolicyCategoryDTO {
    private Long id;
    private String category;
    private String color;
}

