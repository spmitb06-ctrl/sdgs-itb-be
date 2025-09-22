package com.sdgs.itb.infrastructure.faculty.controller;

import com.sdgs.itb.common.responses.ApiResponse;
import com.sdgs.itb.infrastructure.faculty.dto.FacultyDTO;
import com.sdgs.itb.service.faculty.FacultyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/faculties")
@CrossOrigin(origins = "*")
public class FacultyController {

    private final FacultyService facultyService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<FacultyDTO>>> getAll() {
        List<FacultyDTO> faculties = facultyService.getAllFaculties();
        if (faculties.isEmpty()) {
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "No faculties found",
                    faculties
            );
        }
        return ApiResponse.success(
                HttpStatus.OK.value(),
                "Faculties fetched successfully",
                faculties
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FacultyDTO>> getOne(@PathVariable Long id) {
        try {
            FacultyDTO faculty = facultyService.getFaculty(id);
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "Faculty fetched successfully",
                    faculty
            );
        } catch (Exception e) {
            return ApiResponse.failed(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage()
            );
        }
    }
}


