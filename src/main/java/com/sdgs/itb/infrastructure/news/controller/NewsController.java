package com.sdgs.itb.infrastructure.news.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdgs.itb.common.responses.ApiResponse;
import com.sdgs.itb.infrastructure.news.dto.NewsCategoryStatsDTO;
import com.sdgs.itb.infrastructure.news.dto.NewsDTO;
import com.sdgs.itb.infrastructure.news.dto.NewsGoalStatsDTO;
import com.sdgs.itb.service.cloudinary.CloudinaryService;
import com.sdgs.itb.service.news.NewsService;
import com.sdgs.itb.service.news.NewsStatsService;
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
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NewsController {

    private final NewsService newsService;
    private final NewsStatsService newsStatsService;
    private final CloudinaryService cloudinaryService;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<NewsDTO>> create(
            @RequestPart("news") String newsJson,
            @RequestPart(value = "images", required = false) MultipartFile[] images
    ) {
        try {
            NewsDTO dto = objectMapper.readValue(newsJson, NewsDTO.class);

            List<String> imageUrls = new ArrayList<>();
            if (images != null) {
                for (MultipartFile file : images) {
                    String uploadedUrl = cloudinaryService.uploadImage(file);
                    imageUrls.add(uploadedUrl);
                }
                dto.setImageUrls(imageUrls);
            }

            NewsDTO responseDto = newsService.createNews(dto);
            return ApiResponse.success(HttpStatus.CREATED.value(), "News created successfully", responseDto);
        } catch (Exception e) {
            return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<NewsDTO>> update(
            @PathVariable Long id,
            @RequestPart("news") String newsJson,
            @RequestPart(value = "images", required = false) MultipartFile[] images
    ) {
        try {
            NewsDTO dto = objectMapper.readValue(newsJson, NewsDTO.class);

            List<String> imageUrls = new ArrayList<>();
            if (images != null && images.length > 0) {
                for (MultipartFile file : images) {
                    String imageUrl = cloudinaryService.uploadImage(file);
                    imageUrls.add(imageUrl);
                }
                dto.setImageUrls(imageUrls);
            }

            NewsDTO updatedNews = newsService.updateNews(id, dto);
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "News updated successfully",
                    updatedNews
            );
        } catch (Exception e) {
            return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        try {
            newsService.deleteNews(id);
            return ApiResponse.success(
                    HttpStatus.OK.value(), // ✅ use 200 OK for ApiResponse
                    "News deleted successfully",
                    null
            );
        } catch (Exception e) {
            return ApiResponse.failed(HttpStatus.NOT_FOUND.value(), e.getMessage());
        }
    }

    @DeleteMapping("/image/{imageId}")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable Long imageId) {
        try {
            newsService.deleteNewsImage(imageId);
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
    public ResponseEntity<ApiResponse<NewsDTO>> getOne(@PathVariable Long id) {
        try {
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "News fetched successfully",
                    newsService.getNews(id)
            );
        } catch (Exception e) {
            return ApiResponse.failed(HttpStatus.NOT_FOUND.value(), e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<NewsDTO>>> getAll(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String goalIds,     // comma-separated
            @RequestParam(required = false) String categoryIds, // comma-separated
            @RequestParam(required = false) String scholarIds, // comma-separated
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

        List<Long> scholarIdList = scholarIds != null && !scholarIds.isEmpty()
                ? Arrays.stream(scholarIds.split(",")).map(Long::parseLong).toList()
                : List.of();

        List<Long> unitIdList = unitIds != null && !unitIds.isEmpty()
                ? Arrays.stream(unitIds.split(",")).map(Long::parseLong).toList()
                : List.of();

        return ApiResponse.success(
                HttpStatus.OK.value(),
                "News fetched successfully",
                newsService.getNews(
                        title,
                        goalIdList,
                        categoryIdList,
                        scholarIdList,
                        unitIdList,
                        year,
                        sortBy,
                        sortDir,
                        page,
                        size
                )
        );
    }

    @GetMapping("/stats/goals")
    public ResponseEntity<ApiResponse<List<NewsGoalStatsDTO>>> getNewsStatsByGoal(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long categoryId
    ) {
        try {
            List<NewsGoalStatsDTO> stats = newsStatsService.getNewsStatsByGoal(year, categoryId);
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "News statistics by goal fetched successfully",
                    stats
            );
        } catch (Exception e) {
            return ApiResponse.failed(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @GetMapping("/stats/categories")
    public ResponseEntity<ApiResponse<List<NewsCategoryStatsDTO>>> getNewsStatsByCategory(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long goalId
    ) {
        try {
            List<NewsCategoryStatsDTO> stats = newsStatsService.getNewsStatsByCategory(year, goalId);
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "News statistics by category fetched successfully",
                    stats
            );
        } catch (Exception e) {
            return ApiResponse.failed(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }
}


