package com.sdgs.itb.infrastructure.policy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Long categoryId;
    private String categoryName;
}
