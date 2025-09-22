package com.sdgs.itb.infrastructure.sdgs.controller;

import com.sdgs.itb.common.responses.ApiResponse;
import com.sdgs.itb.infrastructure.sdgs.dto.ScholarDTO;
import com.sdgs.itb.service.sdgs.ScholarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/scholars")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ScholarController {

    private final ScholarService scholarService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ScholarDTO>>> getAll() {
        List<ScholarDTO> scholars = scholarService.getAllScholars();
        return ApiResponse.success(
                HttpStatus.OK.value(),
                "Scholars fetched successfully",
                scholars
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ScholarDTO>> create(@RequestBody ScholarDTO dto) {
        ScholarDTO created = scholarService.createScholar(dto);
        return ApiResponse.success(
                HttpStatus.CREATED.value(),
                "Scholar created successfully",
                created
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ScholarDTO>> update(@PathVariable Long id, @RequestBody ScholarDTO dto) {
        ScholarDTO updated = scholarService.updateScholar(id, dto);
        return ApiResponse.success(
                HttpStatus.OK.value(),
                "Scholar updated successfully",
                updated
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        scholarService.deleteScholar(id);
        return ApiResponse.success(
                HttpStatus.NO_CONTENT.value(),
                "Scholar deleted successfully",
                null
        );
    }
}
