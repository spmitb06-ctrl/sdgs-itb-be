package com.sdgs.itb.infrastructure.typesense.controller;

import com.sdgs.itb.common.responses.ApiResponse;
import com.sdgs.itb.service.typesense.TypesenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/typesense")
@RequiredArgsConstructor
public class TypesenseSyncController {

    private final TypesenseService typesenseService;

    private static final String[] COLLECTIONS = {"paper", "project", "patent", "outreach", "thesis"};

    /**
     * Incremental sync with early-exit and optional year filtering.
     *
     * Example calls:
     * POST /api/v1/typesense/sync
     * POST /api/v1/typesense/sync?table=paper
     * POST /api/v1/typesense/sync?table=paper&year=2026
     * POST /api/v1/typesense/sync?threshold=50
     */
    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> syncIncremental(
            @RequestParam(required = false) String table,
            @RequestParam(required = false) Integer year,
            @RequestParam(defaultValue = "30") int threshold) {

        Map<String, Object> results = new LinkedHashMap<>();

        if (table != null && !table.trim().isEmpty()) {
            int added = typesenseService.importIncremental(table.trim(), year, threshold);
            results.put(table.trim(), added);
        } else {
            int totalAdded = 0;
            for (String col : COLLECTIONS) {
                int added = typesenseService.importIncremental(col, year, threshold);
                results.put(col, added);
                totalAdded += added;
            }
            results.put("total_new_records", totalAdded);
        }

        return ResponseEntity.ok(results);
    }

    /**
     * Dedicated fast backfill endpoint for paper metadata (cite, sjr, source).
     *
     * Examples:
     * POST /api/v1/typesense/backfill/paper
     * POST /api/v1/typesense/backfill/paper?year=2026
     */
    @PostMapping("/backfill/paper")
    public ResponseEntity<ApiResponse<Map<String, Object>>> backfillPaper(
            @RequestParam(required = false) Integer year
    ) {
        try {
            int updatedCount = typesenseService.backfillPaperMetadata(year);
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "Backfill completed successfully",
                    Map.of("targetYear", year != null ? year : "all >= 2024", "recordsUpdated", updatedCount)
            );
        } catch (Exception e) {
            return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }
}
