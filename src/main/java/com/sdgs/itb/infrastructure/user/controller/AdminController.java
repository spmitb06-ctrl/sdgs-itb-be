package com.sdgs.itb.infrastructure.user.controller;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.sdgs.itb.common.exceptions.DataNotFoundException;
import com.sdgs.itb.common.exceptions.EmailAlreadyExistsException;
import com.sdgs.itb.common.exceptions.UnauthorizedException;
import com.sdgs.itb.common.responses.ApiResponse;
import com.sdgs.itb.infrastructure.user.dto.UserDTO;
import com.sdgs.itb.service.user.AdminService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admins")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO userDTO) {
        try {
            return ApiResponse.success(
                    HttpStatus.CREATED.value(),
                    "Register user success!",
                    adminService.registerUser(userDTO)
            );
        } catch (EmailAlreadyExistsException e) {
            return ApiResponse.failed(e.getMessage());
        } catch (UnauthorizedException e) {
            return ApiResponse.failed(HttpStatus.FORBIDDEN.value(), "Unauthorized: " + e.getMessage());
        } catch (MessagingException e) {
            return ApiResponse.failed("Failed to send verification email: " + e.getMessage());
        }
    }

    @PostMapping("/complete-registration")
    public ResponseEntity<?> completeRegistration(@RequestParam String token, @Valid @RequestBody UserDTO userDTO) {
        try {
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "Registration completed successfully!",
                    adminService.completeRegistration(token, userDTO)
            );
        } catch (TokenExpiredException e) {
            return ApiResponse.failed(HttpStatus.UNAUTHORIZED.value(), "Token expired: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        try {
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "Update user success!",
                    adminService.updateUser(id, userDTO)
            );
        } catch (EmailAlreadyExistsException e) {
            return ApiResponse.failed(e.getMessage());
        } catch (UnauthorizedException e) {
            return ApiResponse.failed(HttpStatus.FORBIDDEN.value(), "Unauthorized: " + e.getMessage());
        } catch (DataNotFoundException e) {
            return ApiResponse.failed(HttpStatus.NOT_FOUND.value(), "Data not found: " + e.getMessage());
        } catch (MessagingException e) {
            return ApiResponse.failed("Failed to send email: " + e.getMessage());
        }
    }

    @PostMapping("/confirm-email-change")
    public ResponseEntity<?> confirmEmailChange(@RequestParam String token, @RequestParam String newEmail) {
        try {
            adminService.confirmEmailChange(token, newEmail);
            return ApiResponse.success(HttpStatus.OK.value(), "Email change confirmed!", null);
        } catch (TokenExpiredException e) {
            return ApiResponse.failed(HttpStatus.UNAUTHORIZED.value(), "Token expired: " + e.getMessage());
        }
    }

    @PostMapping("/request-password-change/{userId}")
    public ResponseEntity<?> requestPasswordChange(@PathVariable Long userId) {
        try {
            adminService.requestPasswordChange(userId);
            return ApiResponse.success(HttpStatus.OK.value(), "Password change email sent!", null);
        } catch (DataNotFoundException e) {
            return ApiResponse.failed(HttpStatus.NOT_FOUND.value(), "User not found: " + e.getMessage());
        } catch (MessagingException e) {
            return ApiResponse.failed("Failed to send email: " + e.getMessage());
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestParam String token, @RequestParam String newPassword) {
        try {
            adminService.changePassword(token, newPassword);
            return ApiResponse.success(HttpStatus.OK.value(), "Password changed successfully!", null);
        } catch (TokenExpiredException e) {
            return ApiResponse.failed(HttpStatus.UNAUTHORIZED.value(), "Token expired: " + e.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            adminService.forgotPassword(email);
            return ApiResponse.success(HttpStatus.OK.value(), "Password reset email sent!", null);
        } catch (DataNotFoundException e) {
            return ApiResponse.failed(HttpStatus.NOT_FOUND.value(), "User not found: " + e.getMessage());
        } catch (MessagingException e) {
            return ApiResponse.failed("Failed to send email: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            adminService.deleteUser(id);
            return ApiResponse.success(HttpStatus.OK.value(), "User deleted successfully!", null);
        } catch (DataNotFoundException e) {
            return ApiResponse.failed(HttpStatus.NOT_FOUND.value(), "User not found: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        try {
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "Get user success!",
                    adminService.getUser(id)
            );
        } catch (DataNotFoundException e) {
            return ApiResponse.failed(HttpStatus.NOT_FOUND.value(), "User not found: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getUsers(
            @RequestParam(required = false) String roleIds,
            @RequestParam(required = false) String unitIds,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        List<Long> roleIdList = roleIds != null && !roleIds.isEmpty()
                ? Arrays.stream(roleIds.split(",")).map(Long::parseLong).toList()
                : List.of();

        List<Long> unitIdList = unitIds != null && !unitIds.isEmpty()
                ? Arrays.stream(unitIds.split(",")).map(Long::parseLong).toList()
                : List.of();


        return ApiResponse.success(
                HttpStatus.OK.value(),
                "Get users success!",
                adminService.getUsers(roleIdList, unitIdList, sortBy, sortDir, page, size)
        );
    }
}
