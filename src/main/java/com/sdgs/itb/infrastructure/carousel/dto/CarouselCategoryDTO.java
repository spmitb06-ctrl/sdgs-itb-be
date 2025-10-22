package com.sdgs.itb.infrastructure.carousel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarouselCategoryDTO {
    private Long id;
    private String category;
    private String color;
}

