package com.sdgs.itb.infrastructure.news.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsExcelRowDTO {
    private String year;
    private String title;
    private String content;
    private String unitAbbreviation;
}