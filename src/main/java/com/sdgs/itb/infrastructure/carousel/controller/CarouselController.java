package com.sdgs.itb.infrastructure.carousel.controller;

import com.sdgs.itb.common.responses.ApiResponse;
import com.sdgs.itb.infrastructure.carousel.dto.CarouselDTO;
import com.sdgs.itb.service.carousel.CarouselService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/carousel")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CarouselController {

    private final CarouselService carouselService;

    @PostMapping
    public ResponseEntity<ApiResponse<CarouselDTO>> create(@RequestBody CarouselDTO dto) {
        try {
            CarouselDTO responseDto = carouselService.createCarousel(dto);
            return ApiResponse.success(HttpStatus.CREATED.value(), "Carousel created successfully", responseDto);
        } catch (Exception e) {
            return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CarouselDTO>> update(
            @PathVariable Long id,
            @RequestBody CarouselDTO dto
    ) {
        try {
            CarouselDTO updatedCarousel = carouselService.updateCarousel(id, dto);
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "Carousel updated successfully",
                    updatedCarousel
            );
        } catch (Exception e) {
            return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        try {
            carouselService.deleteCarousel(id);
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "Carousel deleted successfully",
                    null
            );
        } catch (Exception e) {
            return ApiResponse.failed(HttpStatus.NOT_FOUND.value(), e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CarouselDTO>> getOne(@PathVariable Long id) {
        try {
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "Carousel fetched successfully",
                    carouselService.getCarousel(id)
            );
        } catch (Exception e) {
            return ApiResponse.failed(HttpStatus.NOT_FOUND.value(), e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CarouselDTO>>> getAll() {
        List<CarouselDTO> carousel = carouselService.getAllCarousel();
        if (carousel.isEmpty()) {
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "No carousel found",
                    carousel
            );
        }
        return ApiResponse.success(
                HttpStatus.OK.value(),
                "Carousel fetched successfully",
                carousel
        );
    }

}


