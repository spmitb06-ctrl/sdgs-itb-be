package com.sdgs.itb.infrastructure.news.controller;

import com.sdgs.itb.common.responses.ApiResponse;
import com.sdgs.itb.infrastructure.news.dto.NewsCategoryDTO;
import com.sdgs.itb.service.unit.UnitService;
import com.sdgs.itb.service.news.NewsCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/news/categories")
@CrossOrigin(origins = "*")
public class NewsCategoryController {

    private final NewsCategoryService newsCategoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NewsCategoryDTO>>> getAll() {
        List<NewsCategoryDTO> newsCategories = newsCategoryService.getAllCategories();
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
    public ResponseEntity<ApiResponse<NewsCategoryDTO>> getOne(@PathVariable Long id) {
        try {
            NewsCategoryDTO newsCategory = newsCategoryService.getCategory(id);
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


