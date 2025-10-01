package com.sdgs.itb.infrastructure.user.controller;

import com.sdgs.itb.common.responses.ApiResponse;
import com.sdgs.itb.infrastructure.user.dto.RoleDTO;
import com.sdgs.itb.service.user.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/roles")
@CrossOrigin(origins = "*")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleDTO>>> getAll() {
        List<RoleDTO> roles = roleService.getRoles();
        if (roles.isEmpty()) {
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "No roles found",
                    roles
            );
        }
        return ApiResponse.success(
                HttpStatus.OK.value(),
                "Roles fetched successfully",
                roles
        );
    }
}


