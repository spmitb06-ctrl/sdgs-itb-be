package com.sdgs.itb.infrastructure.policy.controller;

import com.sdgs.itb.common.responses.ApiResponse;
import com.sdgs.itb.infrastructure.policy.dto.PolicyDTO;
import com.sdgs.itb.service.policy.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/policies")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PolicyController {

    private final PolicyService policyService;

    @PostMapping
    public ResponseEntity<ApiResponse<PolicyDTO>> create(@RequestBody PolicyDTO dto) {
        try {
            PolicyDTO responseDto = policyService.createPolicy(dto);
            return ApiResponse.success(HttpStatus.CREATED.value(), "Policy created successfully", responseDto);
        } catch (Exception e) {
            return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<ApiResponse<PolicyDTO>> update(
            @RequestParam Long id,
            @RequestBody PolicyDTO dto
    ) {
        try {
            PolicyDTO updatedPolicy = policyService.updatePolicy(id, dto);
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "Policy updated successfully",
                    updatedPolicy
            );
        } catch (Exception e) {
            return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> delete(@RequestParam Long id) {
        try {
            policyService.deletePolicy(id);
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "Policy deleted successfully",
                    null
            );
        } catch (Exception e) {
            return ApiResponse.failed(HttpStatus.NOT_FOUND.value(), e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PolicyDTO>> getOne(@PathVariable Long id) {
        try {
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "Policy fetched successfully",
                    policyService.getPolicy(id)
            );
        } catch (Exception e) {
            return ApiResponse.failed(HttpStatus.NOT_FOUND.value(), e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PolicyDTO>>> getAll(
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
                policyService.getPolicies(title, year, categoryIdList, sortBy, sortDir, page, size)
        );
    }

}


