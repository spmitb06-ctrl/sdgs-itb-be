package com.sdgs.itb.infrastructure.typesense.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdgs.itb.common.responses.ApiResponse;
import com.sdgs.itb.infrastructure.typesense.dto.TypesenseCountDTO;
import com.sdgs.itb.service.typesense.TypesenseService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/typesense")
@CrossOrigin(origins = "*")
public class TypesenseController {

    private final TypesenseService typesenseService;

    private static final String[] TABLES = {"paper", "project", "patent", "outreach", "thesis", "profile", "organization"};

    private static final String[] SDGS = {
            "GOAL 1: No Poverty",
            "GOAL 2 : Zero Hunger",
            "GOAL 3: Good Health and Well-being",
            "GOAL 4: Quality Education",
            "GOAL 5: Gender Equality",
            "GOAL 6: Clean Water and Sanitation",
            "GOAL 7: Affordable and Clean Energy",
            "GOAL 8: Decent Work and Economic",
            "GOAL 9: Industry, Innovation and Infrastructure",
            "GOAL 10: Reduced Inequality",
            "GOAL 11: Sustainable Cities and Communities",
            "GOAL 12: Responsible Consumption and Production",
            "GOAL 13: Climate Action",
            "GOAL 14: Life Below Water",
            "GOAL 15: Life on Land",
            "GOAL 16: Peace and Justice Strong Institutions",
            "GOAL 17: Partnership for the Goal"
    };

    @GetMapping("/counts")
    public ResponseEntity<ApiResponse<List<TypesenseCountDTO>>> getCounts(
            @RequestParam(required = false) String table,
            @RequestParam(required = false) String sdg
    ) {
        List<TypesenseCountDTO> results = new ArrayList<>();

        String[] targetTables = (table != null) ? new String[]{table} : TABLES;
        String[] targetSdgs = (sdg != null) ? new String[]{sdg} : SDGS;

        for (String t : targetTables) {
            for (String s : targetSdgs) {
                int totalCount = typesenseService.searchCount(t, s);
                results.add(new TypesenseCountDTO(t, s, totalCount));
            }
        }

        return ApiResponse.success(
                HttpStatus.OK.value(),
                "Counts retrieved successfully",
                results
        );
    }

    @PostMapping("/import/all")
    public ResponseEntity<ApiResponse<String>> importDirect(
            @RequestParam String table
    ) {
        typesenseService.importAllFromTypesense(table);
        return ApiResponse.success(HttpStatus.OK.value(), "Direct import completed", "Imported from " + table);
    }

    @PostMapping("/import/sample")
    public ResponseEntity<ApiResponse<String>> importSampleDirect(
            @RequestParam String table
    ) {
        typesenseService.importSampleFromTypesense(table);
        return ApiResponse.success(HttpStatus.OK.value(), "Direct import sample completed", "Imported from " + table);
    }

    @GetMapping("/documents/export")
    public void exportDocumentsAsStream(
            @RequestParam String table,
            HttpServletResponse response
    ) {
        typesenseService.streamExport(table, response);
    }

}

