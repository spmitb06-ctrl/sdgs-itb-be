package com.sdgs.itb.infrastructure.news.dto;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewsDTO {
    private Long id;
    private String title;
    private String content;
    private String thumbnailUrl;
    private String sourceUrl;
    private List<String> imageUrls;
    private List<Long> goalIds;
    private Long categoryId;
    private String categoryName;
    private Long scholarId;
    private String scholarName;
    private List<Long> unitIds;
    private OffsetDateTime createdAt;

    private List<NewsGoalDTO> newsGoals;
    private List<NewsUnitDTO> newsUnits;
}
