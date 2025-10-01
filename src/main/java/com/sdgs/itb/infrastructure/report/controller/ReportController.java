package com.sdgs.itb.infrastructure.report.controller;

import com.sdgs.itb.common.responses.ApiResponse;
import com.sdgs.itb.infrastructure.report.dto.ReportDTO;
import com.sdgs.itb.service.report.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReportDTO>> create(@RequestBody ReportDTO dto) {
        try {
            ReportDTO responseDto = reportService.createReport(dto);
            return ApiResponse.success(HttpStatus.CREATED.value(), "Report created successfully", responseDto);
        } catch (Exception e) {
            return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<ApiResponse<ReportDTO>> update(
            @RequestParam Long id,
            @RequestBody ReportDTO dto
    ) {
        try {
            ReportDTO updatedReport = reportService.updateReport(id, dto);
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "Report updated successfully",
                    updatedReport
            );
        } catch (Exception e) {
            return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> delete(@RequestParam Long id) {
        try {
            reportService.deleteReport(id);
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "Report deleted successfully",
                    null
            );
        } catch (Exception e) {
            return ApiResponse.failed(HttpStatus.NOT_FOUND.value(), e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReportDTO>> getOne(@PathVariable Long id) {
        try {
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "Report fetched successfully",
                    reportService.getReport(id)
            );
        } catch (Exception e) {
            return ApiResponse.failed(HttpStatus.NOT_FOUND.value(), e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ReportDTO>>> getAll(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String year,
            @RequestParam(required = false) String categoryIds, // comma-separated
            @RequestParam(defaultValue = "year") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        List<Long> categoryIdList = categoryIds != null && !categoryIds.isEmpty()
                ? Arrays.stream(categoryIds.split(",")).map(Long::parseLong).toList()
                : List.of();

        return ApiResponse.success(
                HttpStatus.OK.value(),
                "Policies fetched successfully",
                reportService.getPolicies(title, year, categoryIdList, sortBy, sortDir, page, size)
        );
    }

}


