package com.sdgs.itb.service.carousel.impl;

import com.sdgs.itb.common.exceptions.DataNotFoundException;
import com.sdgs.itb.entity.goal.Goal;
import com.sdgs.itb.entity.carousel.Carousel;
import com.sdgs.itb.entity.carousel.CarouselCategory;
import com.sdgs.itb.entity.carousel.CarouselGoal;
import com.sdgs.itb.infrastructure.goal.repository.GoalRepository;
import com.sdgs.itb.infrastructure.carousel.dto.CarouselDTO;
import com.sdgs.itb.infrastructure.carousel.mapper.CarouselMapper;
import com.sdgs.itb.infrastructure.carousel.repository.CarouselCategoryRepository;
import com.sdgs.itb.infrastructure.carousel.repository.CarouselGoalRepository;
import com.sdgs.itb.infrastructure.carousel.repository.CarouselRepository;
import com.sdgs.itb.service.carousel.CarouselService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarouselServiceImpl implements CarouselService {

    private final CarouselRepository carouselRepository;
    private final CarouselGoalRepository carouselGoalRepository;
    private final CarouselCategoryRepository categoryRepository;
    private final GoalRepository goalRepository;

    @Override
    public CarouselDTO createCarousel(CarouselDTO dto) {
        Carousel carousel = CarouselMapper.toEntity(dto);

        CarouselCategory category = null;
        if (dto.getCategoryId() != null) {
            category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new DataNotFoundException("Category not found"));
            carousel.setCarouselCategory(category);
        }

        Carousel savedCarousel = carouselRepository.saveAndFlush(carousel);

        // Goals
        if (dto.getGoalIds() != null && !dto.getGoalIds().isEmpty()) {
            List<Goal> goals = goalRepository.findAllById(dto.getGoalIds());
            if (goals.isEmpty()) throw new DataNotFoundException("No valid goals found");

            for (Goal goal : goals) { // Regular for-loop
                CarouselGoal ag = CarouselGoal.builder()
                        .carousel(savedCarousel)
                        .goal(goal)
                        .build();
                savedCarousel.getCarouselGoals().add(ag);
            }
        }

        return CarouselMapper.toDTO(carouselRepository.save(carousel));
    }

    @Transactional
    @Override
    public CarouselDTO updateCarousel(Long id, CarouselDTO dto) {
        Carousel existing = carouselRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carousel not found"));

        existing.setTitle(dto.getTitle());
        existing.setSubtitle(dto.getSubtitle());
        existing.setSourceUrl(dto.getSourceUrl());
        existing.setImageUrl(dto.getImageUrl());

        CarouselCategory category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Category not found"));

        existing.setCarouselCategory(category);

        // Goals
        if (dto.getGoalIds() != null && !dto.getGoalIds().isEmpty()) {
            List<Goal> carouselGoals = goalRepository.findAllById(dto.getGoalIds());
            if (carouselGoals.isEmpty()) throw new DataNotFoundException("No valid goals found");

            // Fetch all current active CarouselGoal relations for this Carousel
            List<CarouselGoal> existingCarouselGoals = carouselGoalRepository.findAllByCarouselId(existing.getId());

            // Hard delete CarouselGoal records that are not in dto.getGoalIds()
            for (CarouselGoal oldCarouselGoal : existingCarouselGoals) {
                if (!dto.getGoalIds().contains(oldCarouselGoal.getGoal().getId())) {
                    carouselGoalRepository.delete(oldCarouselGoal);
                }
            }

            // Remove goals that already exist (to avoid duplicates)
            carouselGoals.removeIf(goal ->
                    carouselGoalRepository.findByCarouselIdAndGoalId(existing.getId(), goal.getId()).isPresent()
            );

            // Add new CarouselGoal entries for remaining goals
            for (Goal goal : carouselGoals) {
                CarouselGoal newCarouselGoal = CarouselGoal.builder()
                        .carousel(existing)
                        .goal(goal)
                        .build();
                existing.getCarouselGoals().add(newCarouselGoal);
            }
        }

        return CarouselMapper.toDTO(carouselRepository.save(existing));
    }

    @Override
    public void deleteCarousel(Long id) {
        Carousel carousel = carouselRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carousel not found"));

        carouselRepository.delete(carousel);
    }

    @Override
    public CarouselDTO getCarousel(Long id) {
        return carouselRepository.findById(id)
                .filter(carousel -> carousel.getDeletedAt() == null)
                .map(CarouselMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Carousel not found"));
    }

    @Override
    public List<CarouselDTO> getAllCarousel() {
        return carouselRepository.findAll()
                .stream()
                .map(CarouselMapper::toDTO)
                .collect(Collectors.toList());
    }
}
