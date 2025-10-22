package com.sdgs.itb.infrastructure.carousel.repository;

import com.sdgs.itb.entity.carousel.CarouselGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CarouselGoalRepository extends JpaRepository<CarouselGoal, Long> {

    @Query("""
        SELECT cg FROM CarouselGoal cg
        WHERE cg.carousel.id = :carouselId
          AND cg.deletedAt IS NULL
    """)
    List<CarouselGoal> findAllByCarouselId(@Param("carouselId") Long carouselId);

    @Query("""
        SELECT cg
        FROM CarouselGoal cg
        WHERE cg.carousel.id = :carouselId
          AND cg.goal.id = :goalId
          AND cg.deletedAt IS NULL
    """)
    Optional<CarouselGoal> findByCarouselIdAndGoalId(
            @Param("carouselId") Long carouselId,
            @Param("goalId") Long goalId
    );
}
