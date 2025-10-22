package com.sdgs.itb.infrastructure.carousel.dto;

import com.sdgs.itb.infrastructure.carousel.dto.CarouselGoalDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarouselDTO {
    private Long id;
    private String title;
    private String subtitle;
    private String imageUrl;
    private String sourceUrl;
    private Long categoryId;
    private String categoryName;
    private String categoryColor;
    private List<Long> goalIds;

    private List<CarouselGoalDTO> carouselGoals;
}
