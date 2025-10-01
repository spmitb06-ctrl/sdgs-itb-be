package com.sdgs.itb.infrastructure.unit.controller;

import com.sdgs.itb.common.responses.ApiResponse;
import com.sdgs.itb.infrastructure.unit.dto.UnitDTO;
import com.sdgs.itb.service.unit.UnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/units")
@CrossOrigin(origins = "*")
public class UnitController {

    private final UnitService unitService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UnitDTO>>> getAll() {
        List<UnitDTO> units = unitService.getAllUnits();
        if (units.isEmpty()) {
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "No units found",
                    units
            );
        }
        return ApiResponse.success(
                HttpStatus.OK.value(),
                "Units fetched successfully",
                units
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UnitDTO>> getOne(@PathVariable Long id) {
        try {
            UnitDTO unit = unitService.getUnit(id);
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "Unit fetched successfully",
                    unit
            );
        } catch (Exception e) {
            return ApiResponse.failed(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage()
            );
        }
    }
}


