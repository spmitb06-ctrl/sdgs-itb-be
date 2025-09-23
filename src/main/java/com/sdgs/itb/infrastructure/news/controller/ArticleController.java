package com.sdgs.itb.infrastructure.news.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdgs.itb.common.responses.ApiResponse;
import com.sdgs.itb.infrastructure.news.dto.ArticleDTO;
import com.sdgs.itb.service.cloudinary.CloudinaryService;
import com.sdgs.itb.service.news.ArticleService;
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
@RequestMapping("/api/v1/articles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ArticleController {

    private final ArticleService articleService;
    private final CloudinaryService cloudinaryService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<ArticleDTO>> create(
            @RequestPart("article") String articleJson,
            @RequestPart(value = "images", required = false) MultipartFile[] images
    ) {
        try {
            ArticleDTO dto = objectMapper.readValue(articleJson, ArticleDTO.class);

            List<String> imageUrls = new ArrayList<>();
            if (images != null) {
                for (MultipartFile file : images) {
                    String uploadedUrl = cloudinaryService.uploadImage(file);
                    imageUrls.add(uploadedUrl);
                }
                dto.setImageUrls(imageUrls);
            }

            ArticleDTO responseDto = articleService.createArticle(dto);
            return ApiResponse.success(HttpStatus.CREATED.value(), "Article created successfully", responseDto);
        } catch (Exception e) {
            return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<ArticleDTO>> update(
            @PathVariable Long id,
            @RequestPart("article") String articleJson,
            @RequestPart(value = "images", required = false) MultipartFile[] images
    ) {
        try {
            ArticleDTO dto = objectMapper.readValue(articleJson, ArticleDTO.class);

            List<String> imageUrls = new ArrayList<>();
            if (images != null && images.length > 0) {
                for (MultipartFile file : images) {
                    String imageUrl = cloudinaryService.uploadImage(file);
                    imageUrls.add(imageUrl);
                }
                dto.setImageUrls(imageUrls);
            }

            ArticleDTO updatedArticle = articleService.updateArticle(id, dto);
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "Article updated successfully",
                    updatedArticle
            );
        } catch (Exception e) {
            return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        try {
            articleService.deleteArticle(id);
            return ApiResponse.success(
                    HttpStatus.OK.value(), // ✅ use 200 OK for ApiResponse
                    "Article deleted successfully",
                    null
            );
        } catch (Exception e) {
            return ApiResponse.failed(HttpStatus.NOT_FOUND.value(), e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ArticleDTO>> getOne(@PathVariable Long id) {
        try {
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "Article fetched successfully",
                    articleService.getArticle(id)
            );
        } catch (Exception e) {
            return ApiResponse.failed(HttpStatus.NOT_FOUND.value(), e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ArticleDTO>>> getAll(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String goalIds,     // comma-separated
            @RequestParam(required = false) String categoryIds, // comma-separated
            @RequestParam(required = false) String scholarIds, // comma-separated
            @RequestParam(required = false) String facultyIds,  // comma-separated
            @RequestParam(required = false) String year,
            @RequestParam(defaultValue = "createdAt") String sortBy,
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

        List<Long> scholarIdList = scholarIds != null && !scholarIds.isEmpty()
                ? Arrays.stream(scholarIds.split(",")).map(Long::parseLong).toList()
                : List.of();

        List<Long> facultyIdList = facultyIds != null && !facultyIds.isEmpty()
                ? Arrays.stream(facultyIds.split(",")).map(Long::parseLong).toList()
                : List.of();

        return ApiResponse.success(
                HttpStatus.OK.value(),
                "Articles fetched successfully",
                articleService.getArticles(title, goalIdList, categoryIdList, scholarIdList, facultyIdList, year, sortBy, sortDir, page, size)
        );
    }

}


