package com.sdgs.itb.infrastructure.typesense.controller;

import com.sdgs.itb.service.typesense.TypesenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/typesense/sync")
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
    @PostMapping
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
}
