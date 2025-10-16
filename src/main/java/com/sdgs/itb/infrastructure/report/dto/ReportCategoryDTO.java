package com.sdgs.itb.infrastructure.report.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportCategoryDTO {
    private Long id;
    private String category;
    private String color;
    private String iconUrl;
}

