package com.sdgs.itb.infrastructure.carousel.mapper;

import com.sdgs.itb.entity.goal.Goal;
import com.sdgs.itb.entity.carousel.Carousel;
import com.sdgs.itb.entity.carousel.CarouselGoal;
import com.sdgs.itb.infrastructure.goal.dto.GoalDTO;
import com.sdgs.itb.infrastructure.carousel.dto.CarouselDTO;
import com.sdgs.itb.infrastructure.carousel.dto.CarouselGoalDTO;

import java.util.stream.Collectors;

public class CarouselMapper {
    public static Carousel toEntity(CarouselDTO dto) {
        Carousel carousel = new Carousel();
        carousel.setTitle(dto.getTitle());
        carousel.setSubtitle(dto.getSubtitle());
        carousel.setImageUrl(dto.getImageUrl());
        carousel.setSourceUrl(dto.getSourceUrl());

        return carousel;
    }

    public static CarouselDTO toDTO(Carousel carousel) {
        CarouselDTO dto = new CarouselDTO();
        dto.setId(carousel.getId());
        dto.setTitle(carousel.getTitle());
        dto.setSubtitle(carousel.getSubtitle());
        dto.setImageUrl(carousel.getImageUrl());
        dto.setSourceUrl(carousel.getSourceUrl());

        dto.setCarouselGoals(carousel.getCarouselGoals().stream()
                .map(CarouselMapper::toCarouselGoalDTO)
                .collect(Collectors.toList()));

        if (carousel.getCarouselCategory() != null) {
            dto.setCategoryId(carousel.getCarouselCategory().getId());
            dto.setCategoryName(carousel.getCarouselCategory().getCategory());
            dto.setCategoryColor(carousel.getCarouselCategory().getColor());
        }

        return dto;
    }

    public static GoalDTO toGoalDTO(Goal goal) {
        GoalDTO dto = new GoalDTO();
        dto.setId(goal.getId());
        dto.setGoalNumber(goal.getGoalNumber());
        dto.setTitle(goal.getTitle());
        dto.setDescription(goal.getDescription());
        dto.setColor(goal.getColor());
        dto.setIcon(goal.getIcon());
        dto.setEditedIcon(goal.getEditedIcon());
        dto.setLinkUrl(goal.getLinkUrl());
        return dto;
    }

    public static CarouselGoalDTO toCarouselGoalDTO(CarouselGoal carouselGoal) {
        CarouselGoalDTO dto = new CarouselGoalDTO();
        dto.setId(carouselGoal.getId());
        dto.setGoals(toGoalDTO(carouselGoal.getGoal()));
        return dto;
    }
}

