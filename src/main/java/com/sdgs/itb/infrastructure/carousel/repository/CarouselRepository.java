package com.sdgs.itb.infrastructure.carousel.repository;

import com.sdgs.itb.entity.carousel.Carousel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CarouselRepository extends JpaRepository<Carousel, Long> {

    @Query("SELECT a FROM Carousel a WHERE LOWER(a.sourceUrl) = LOWER(:url)")
    Optional<Carousel> findBySourceUrl(@Param("url") String url);

    @Query("SELECT a FROM Carousel a WHERE LOWER(a.title) = LOWER(:title)")
    Optional<Carousel> findByTitleIgnoreCase(@Param("title") String title);
}
