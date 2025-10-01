package com.sdgs.itb.infrastructure.report.controller;

import com.sdgs.itb.common.responses.ApiResponse;
import com.sdgs.itb.infrastructure.report.dto.ReportCategoryDTO;
import com.sdgs.itb.service.report.ReportCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/report-categories")
@CrossOrigin(origins = "*")
public class ReportCategoryController {

    private final ReportCategoryService reportCategoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReportCategoryDTO>>> getAll() {
        List<ReportCategoryDTO> newsCategories = reportCategoryService.getAllCategories();
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
    public ResponseEntity<ApiResponse<ReportCategoryDTO>> getOne(@PathVariable Long id) {
        try {
            ReportCategoryDTO newsCategory = reportCategoryService.getCategory(id);
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


