package com.sdgs.itb.infrastructure.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportDTO {
    private Long id;
    private String title;
    private String description;
    private String fileUrl;
    private String year;
    private String imageUrl;
    private Long categoryId;
    private String categoryName;
    private String categoryColor;
}
