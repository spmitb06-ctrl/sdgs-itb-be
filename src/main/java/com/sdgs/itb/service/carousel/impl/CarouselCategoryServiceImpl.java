package com.sdgs.itb.service.carousel.impl;

import com.sdgs.itb.entity.carousel.CarouselCategory;
import com.sdgs.itb.infrastructure.carousel.dto.CarouselCategoryDTO;
import com.sdgs.itb.infrastructure.carousel.mapper.CarouselCategoryMapper;
import com.sdgs.itb.infrastructure.carousel.repository.CarouselCategoryRepository;
import com.sdgs.itb.service.carousel.CarouselCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarouselCategoryServiceImpl implements CarouselCategoryService {

    private final CarouselCategoryRepository carouselCategoryRepository;

    @Override
    public List<CarouselCategoryDTO> getAllCategories() {
        return carouselCategoryRepository.findAll()
                .stream()
                .map(CarouselCategoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CarouselCategoryDTO getCategory(Long id) {
        CarouselCategory carouselCategory = carouselCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carousel category not found"));
        return CarouselCategoryMapper.toDTO((carouselCategory));
    }
}
