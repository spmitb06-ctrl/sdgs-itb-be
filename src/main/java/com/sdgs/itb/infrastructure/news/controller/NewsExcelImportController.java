package com.sdgs.itb.infrastructure.news.controller;

import com.sdgs.itb.service.news.NewsExcelImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
public class NewsExcelImportController {

    private final NewsExcelImportService newsExcelImportService;

    @PostMapping(value = "/import/excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadNewsExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Please provide a valid Excel file (.xlsx)"
            ));
        }

        int count = newsExcelImportService.importNewsFromExcel(file);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Successfully imported " + count + " news records",
                "importedCount", count
        ));
    }
}