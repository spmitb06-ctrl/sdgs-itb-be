package com.sdgs.itb.infrastructure.data.controller;

import com.sdgs.itb.common.responses.ApiResponse;
import com.sdgs.itb.infrastructure.data.dto.DataCategoryDTO;
import com.sdgs.itb.service.data.DataCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/data/categories")
@CrossOrigin(origins = "*")
public class DataCategoryController {

    private final DataCategoryService dataCategoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<DataCategoryDTO>> create(@RequestBody DataCategoryDTO dto) {
        try {
            DataCategoryDTO created = dataCategoryService.createCategory(dto);
            return ApiResponse.success(
                    HttpStatus.CREATED.value(),
                    "Category created successfully",
                    created
            );
        } catch (Exception e) {
            return ApiResponse.failed(
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage()
            );
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DataCategoryDTO>> update(@PathVariable Long id, @RequestBody DataCategoryDTO dto) {
        try {
            DataCategoryDTO updated = dataCategoryService.updateCategory(id, dto);
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "Category updated successfully",
                    updated
            );
        } catch (Exception e) {
            return ApiResponse.failed(
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage()
            );
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        try {
            dataCategoryService.deleteCategory(id);
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "Category deleted successfully",
                    null
            );
        } catch (Exception e) {
            return ApiResponse.failed(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage()
            );
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DataCategoryDTO>>> getAll() {
        List<DataCategoryDTO> dataCategories = dataCategoryService.getCategories();
        if (dataCategories.isEmpty()) {
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "No category found",
                    dataCategories
            );
        }
        return ApiResponse.success(
                HttpStatus.OK.value(),
                "Categories fetched successfully",
                dataCategories
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DataCategoryDTO>> getOne(@PathVariable Long id) {
        try {
            DataCategoryDTO dataCategory = dataCategoryService.getCategory(id);
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "Category fetched successfully",
                    dataCategory
            );
        } catch (Exception e) {
            return ApiResponse.failed(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage()
            );
        }
    }
}


