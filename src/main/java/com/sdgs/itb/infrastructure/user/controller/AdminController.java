package com.sdgs.itb.infrastructure.user.controller;

import com.sdgs.itb.common.exceptions.EmailAlreadyExistsException;
import com.sdgs.itb.common.responses.ApiResponse;
import com.sdgs.itb.infrastructure.user.dto.UserDTO;
import com.sdgs.itb.service.user.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admins")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO userDTO) {
        try {
            return ApiResponse.success(HttpStatus.OK.value(), "Register user success!", adminService.registerUser(userDTO));
        } catch (EmailAlreadyExistsException e) {
            return ApiResponse.failed(e.getMessage());
        }
    }
}
