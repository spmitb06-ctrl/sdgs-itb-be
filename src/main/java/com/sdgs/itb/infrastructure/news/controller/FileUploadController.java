package com.sdgs.itb.infrastructure.news.controller;

import com.sdgs.itb.common.responses.ApiResponse;
import com.sdgs.itb.service.cloudinary.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FileUploadController {

    private final CloudinaryService cloudinaryService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String uploadedUrl = cloudinaryService.uploadImage(file);
            return ApiResponse.success(HttpStatus.OK.value(), "Upload successful", uploadedUrl);
        } catch (Exception e) {
            return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<String>>> listImages() {
        List<String> urls = cloudinaryService.listAllImages();
        return ApiResponse.success(HttpStatus.OK.value(), "Fetched media successfully", urls);
    }

    @DeleteMapping("/delete/{publicId}")
    public void delete(@PathVariable String publicId) {
        cloudinaryService.deleteFile(publicId);
    }
}

