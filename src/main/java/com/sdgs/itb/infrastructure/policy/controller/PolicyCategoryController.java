package com.sdgs.itb.infrastructure.policy.controller;

import com.sdgs.itb.common.responses.ApiResponse;
import com.sdgs.itb.infrastructure.policy.dto.PolicyCategoryDTO;
import com.sdgs.itb.service.policy.PolicyCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/policy-categories")
@CrossOrigin(origins = "*")
public class PolicyCategoryController {

    private final PolicyCategoryService policyCategoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PolicyCategoryDTO>>> getAll() {
        List<PolicyCategoryDTO> articleCategories = policyCategoryService.getAllCategories();
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
    public ResponseEntity<ApiResponse<PolicyCategoryDTO>> getOne(@PathVariable Long id) {
        try {
            PolicyCategoryDTO articleCategory = policyCategoryService.getCategory(id);
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


