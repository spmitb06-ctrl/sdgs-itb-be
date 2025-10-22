package com.sdgs.itb.infrastructure.carousel.controller;

import com.sdgs.itb.common.responses.ApiResponse;
import com.sdgs.itb.infrastructure.carousel.dto.CarouselCategoryDTO;
import com.sdgs.itb.service.carousel.CarouselCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/carousel-categories")
@CrossOrigin(origins = "*")
public class CarouselCategoryController {

    private final CarouselCategoryService carouselCategoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CarouselCategoryDTO>>> getAll() {
        List<CarouselCategoryDTO> newsCategories = carouselCategoryService.getAllCategories();
        if (newsCategories.isEmpty()) {
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "No category found",
                    newsCategories
            );
        }
        return ApiResponse.success(
                HttpStatus.OK.value(),
                "Categories fetched successfully",
                newsCategories
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CarouselCategoryDTO>> getOne(@PathVariable Long id) {
        try {
            CarouselCategoryDTO newsCategory = carouselCategoryService.getCategory(id);
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "Category fetched successfully",
                    newsCategory
            );
        } catch (Exception e) {
            return ApiResponse.failed(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage()
            );
        }
    }
}


