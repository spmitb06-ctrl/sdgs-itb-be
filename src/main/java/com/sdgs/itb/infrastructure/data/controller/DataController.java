package com.sdgs.itb.infrastructure.data.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdgs.itb.common.responses.ApiResponse;
import com.sdgs.itb.infrastructure.data.dto.DataDTO;
import com.sdgs.itb.service.cloudinary.CloudinaryService;
import com.sdgs.itb.service.data.DataService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/data")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DataController {

    private final DataService dataService;
    private final CloudinaryService cloudinaryService;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<DataDTO>> create(
            @RequestPart("data") String dataJson,
            @RequestPart(value = "images", required = false) MultipartFile[] images
    ) {
        try {
            DataDTO dto = objectMapper.readValue(dataJson, DataDTO.class);

            List<String> imageUrls = new ArrayList<>();
            if (images != null) {
                for (MultipartFile file : images) {
                    String uploadedUrl = cloudinaryService.uploadImage(file);
                    imageUrls.add(uploadedUrl);
                }
                dto.setImageUrls(imageUrls);
            }

            DataDTO responseDto = dataService.createData(dto);
            return ApiResponse.success(HttpStatus.CREATED.value(), "Data created successfully", responseDto);
        } catch (Exception e) {
            return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<DataDTO>> update(
            @PathVariable Long id,
            @RequestPart("data") String dataJson,
            @RequestPart(value = "images", required = false) MultipartFile[] images
    ) {
        try {
            DataDTO dto = objectMapper.readValue(dataJson, DataDTO.class);

            List<String> imageUrls = new ArrayList<>();
            if (images != null && images.length > 0) {
                for (MultipartFile file : images) {
                    String imageUrl = cloudinaryService.uploadImage(file);
                    imageUrls.add(imageUrl);
                }
                dto.setImageUrls(imageUrls);
            }

            DataDTO updatedData = dataService.updateData(id, dto);
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "Data updated successfully",
                    updatedData
            );
        } catch (Exception e) {
            return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        try {
            dataService.deleteData(id);
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "Data deleted successfully",
                    null
            );
        } catch (Exception e) {
            return ApiResponse.failed(HttpStatus.NOT_FOUND.value(), e.getMessage());
        }
    }

    @DeleteMapping("/image/{imageId}")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable Long imageId) {
        try {
            dataService.deleteDataImage(imageId);
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "Image deleted successfully",
                    null
            );
        } catch (Exception e) {
            return ApiResponse.failed(HttpStatus.NOT_FOUND.value(), e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DataDTO>> getOne(@PathVariable Long id) {
        try {
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "Data fetched successfully",
                    dataService.getData(id)
            );
        } catch (Exception e) {
            return ApiResponse.failed(HttpStatus.NOT_FOUND.value(), e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<DataDTO>>> getAll(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String goalIds,     // comma-separated
            @RequestParam(required = false) String categoryIds, // comma-separated
            @RequestParam(required = false) String unitIds,  // comma-separated
            @RequestParam(required = false) String year,
            @RequestParam(defaultValue = "eventDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        List<Long> goalIdList = goalIds != null && !goalIds.isEmpty()
                ? Arrays.stream(goalIds.split(",")).map(Long::parseLong).toList()
                : List.of();

        List<Long> categoryIdList = categoryIds != null && !categoryIds.isEmpty()
                ? Arrays.stream(categoryIds.split(",")).map(Long::parseLong).toList()
                : List.of();

        List<Long> unitIdList = unitIds != null && !unitIds.isEmpty()
                ? Arrays.stream(unitIds.split(",")).map(Long::parseLong).toList()
                : List.of();

        return ApiResponse.success(
                HttpStatus.OK.value(),
                "Data fetched successfully",
                dataService.getData(
                        title,
                        goalIdList,
                        categoryIdList,
                        unitIdList,
                        year,
                        sortBy,
                        sortDir,
                        page,
                        size
                )
        );
    }

}


