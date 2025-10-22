package com.sdgs.itb.service.carousel;

import com.sdgs.itb.infrastructure.carousel.dto.CarouselCategoryDTO;

import java.util.List;

public interface CarouselCategoryService {
//    CarouselCategoryDTO createCarouselCategory(CarouselCategoryDTO dto);
//    CarouselCategoryDTO updateCarouselCategory(Long id, CarouselCategoryDTO dto);
//    void deleteCarouselCategory(Long id);
    List<CarouselCategoryDTO> getAllCategories();
    CarouselCategoryDTO getCategory(Long id);
}

