package com.sdgs.itb.infrastructure.news.controller;

import com.sdgs.itb.common.responses.ApiResponse;
import com.sdgs.itb.infrastructure.faculty.dto.FacultyDTO;
import com.sdgs.itb.infrastructure.news.dto.ArticleCategoryDTO;
import com.sdgs.itb.service.faculty.FacultyService;
import com.sdgs.itb.service.news.ArticleCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/article-categories")
@CrossOrigin(origins = "*")
public class ArticleCategoryController {

    private final ArticleCategoryService articleCategoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ArticleCategoryDTO>>> getAll() {
        List<ArticleCategoryDTO> articleCategories = articleCategoryService.getAllCategories();
        if (articleCategories.isEmpty()) {
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "No category found",
                    articleCategories
            );
        }
        return ApiResponse.success(
                HttpStatus.OK.value(),
                "Categories fetched successfully",
                articleCategories
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ArticleCategoryDTO>> getOne(@PathVariable Long id) {
        try {
            ArticleCategoryDTO articleCategory = articleCategoryService.getCategory(id);
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "Category fetched successfully",
                    articleCategory
            );
        } catch (Exception e) {
            return ApiResponse.failed(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage()
            );
        }
    }
}


