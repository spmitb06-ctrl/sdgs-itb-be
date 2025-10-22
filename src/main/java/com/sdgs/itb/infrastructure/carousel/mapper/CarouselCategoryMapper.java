package com.sdgs.itb.infrastructure.carousel.mapper;

import com.sdgs.itb.entity.carousel.CarouselCategory;
import com.sdgs.itb.infrastructure.carousel.dto.CarouselCategoryDTO;

public class CarouselCategoryMapper {
    public static CarouselCategory toEntity(CarouselCategoryDTO dto) {
        CarouselCategory carouselCategory = new CarouselCategory();
        carouselCategory.setCategory(dto.getCategory());
        carouselCategory.setColor(dto.getColor());

        return carouselCategory;
    }

    public static CarouselCategoryDTO toDTO(CarouselCategory carouselCategory) {
        return CarouselCategoryDTO.builder()
                .id(carouselCategory.getId())
                .category(carouselCategory.getCategory())
                .color((carouselCategory.getColor()))
                .build();
    }
}


