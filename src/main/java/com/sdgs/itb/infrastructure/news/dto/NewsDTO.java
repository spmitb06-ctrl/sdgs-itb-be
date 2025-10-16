package com.sdgs.itb.infrastructure.news.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sdgs.itb.infrastructure.goal.dto.ScholarDTO;
import lombok.*;

import java.time.LocalDate;
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
    private String scholarYear;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventDate;

    private Long categoryId;
    private String categoryName;
    private List<Long> goalIds;
    private List<Long> unitIds;
    private Long scholarId;
    private String scholarName;
    private String scholarIcon;

    private List<NewsCategoryDTO> newsCategories;
    private List<NewsGoalDTO> newsGoals;
    private List<NewsUnitDTO> newsUnits;
    private List<ScholarDTO> newsScholars;
}
