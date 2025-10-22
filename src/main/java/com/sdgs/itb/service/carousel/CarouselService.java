package com.sdgs.itb.service.carousel;

import com.sdgs.itb.infrastructure.carousel.dto.CarouselDTO;

import java.util.List;

public interface CarouselService {
    CarouselDTO createCarousel(CarouselDTO dto);
    CarouselDTO updateCarousel(Long id, CarouselDTO dto);
    void deleteCarousel(Long id);
    CarouselDTO getCarousel(Long id);
    List<CarouselDTO> getAllCarousel();
}
