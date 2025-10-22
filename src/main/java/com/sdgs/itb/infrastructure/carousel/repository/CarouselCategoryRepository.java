package com.sdgs.itb.infrastructure.carousel.repository;

import com.sdgs.itb.entity.carousel.CarouselCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarouselCategoryRepository extends JpaRepository<CarouselCategory, Long> {
}


