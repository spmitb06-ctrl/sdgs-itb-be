package com.sdgs.itb.infrastructure.data.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataDTO {
    private Long id;
    private String title;
    private String content;
    private String thumbnailUrl;
    private String sourceUrl;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventDate;

    private Long  categoryId;
    private String categoryName;

    private List<Long> goalIds;
    private List<Long> unitIds;
    private List<String> imageUrls;

    private List<DataGoalDTO> dataGoals;
    private List<DataUnitDTO> dataUnits;
    private List<DataImageDTO> dataImages;
}
