package com.sdgs.itb.infrastructure.unit.controller;

import com.sdgs.itb.common.responses.ApiResponse;
import com.sdgs.itb.infrastructure.unit.dto.UnitDTO;
import com.sdgs.itb.infrastructure.unit.dto.UnitTypeDTO;
import com.sdgs.itb.service.unit.UnitService;
import com.sdgs.itb.service.unit.UnitTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/unit-type")
@CrossOrigin(origins = "*")
public class UnitTypeController {

    private final UnitTypeService unitTypeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UnitTypeDTO>>> getAll() {
        List<UnitTypeDTO> unitTypes = unitTypeService.getAllUnitTypes();
        if (unitTypes.isEmpty()) {
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "No unit type found",
                    unitTypes
            );
        }
        return ApiResponse.success(
                HttpStatus.OK.value(),
                "Unit Types fetched successfully",
                unitTypes
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UnitTypeDTO>> getOne(@PathVariable Long id) {
        try {
            UnitTypeDTO unitType = unitTypeService.getUnitType(id);
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "Unit type fetched successfully",
                    unitType
            );
        } catch (Exception e) {
            return ApiResponse.failed(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage()
            );
        }
    }
}


