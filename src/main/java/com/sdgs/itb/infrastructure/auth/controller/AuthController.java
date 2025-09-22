package com.sdgs.itb.infrastructure.auth.controller;

import com.sdgs.itb.entity.user.*;
import com.sdgs.itb.infrastructure.auth.dto.AuthDTO;
import com.sdgs.itb.infrastructure.auth.dto.LogoutRequestDTO;
import com.sdgs.itb.infrastructure.user.dto.RegisterUserDTO;
import com.sdgs.itb.infrastructure.user.dto.UserDTO;
import com.sdgs.itb.service.auth.LogoutService;
import com.sdgs.itb.service.auth.impl.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sdgs.itb.common.responses.ApiResponse;
import com.sdgs.itb.infrastructure.auth.Claims;
import com.sdgs.itb.infrastructure.auth.dto.LoginRequestDTO;
import com.sdgs.itb.service.auth.LoginService;
import com.sdgs.itb.service.auth.TokenRefreshService;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    private final LoginService loginService;
    private final TokenRefreshService tokenRefreshService;
    private final LogoutService logoutService;

    public AuthController(
            LoginService loginService,
            TokenRefreshService tokenRefreshService,
            com.sdgs.itb.service.auth.TokenBlacklistService tokenBlacklistService,
            LogoutService logoutService
    ) {
        this.loginService = loginService;
        this.tokenRefreshService = tokenRefreshService;
        this.logoutService = logoutService;

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody LoginRequestDTO req) {
        return ApiResponse.success("Login successful", loginService.authenticateUser(req));
    }

// ---------------------------------------------------
//    @PostMapping("/login")
//    public ResponseEntity<ApiResponse<UserDTO>> login(@RequestBody AuthDTO authDTO) {
//        try {
//            User user = authService.authenticate(authDTO.getEmail(), authDTO.getPassword());
//            UserDTO userDTO = convertToDto(user);
//            return ApiResponse.success(HttpStatus.OK.value(), "Login successful", userDTO);
//        } catch (Exception e) {
//            return ApiResponse.failed(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
//        }
//    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> register(@RequestBody RegisterUserDTO registerDTO) {
        try {
            User user = convertToEntity(registerDTO);
            User registeredUser = authService.registerUser(user, registerDTO.getRoles().toString());
            UserDTO userDTO = convertToDto(registeredUser);
            return ApiResponse.success(HttpStatus.CREATED.value(), "User registered successfully", userDTO);
        } catch (Exception e) {
            return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    private UserDTO convertToDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());

        // Set roles
        dto.setRoles(user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet()));

        return dto;
    }

    private User convertToEntity(RegisterUserDTO dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setName(dto.getName());

        return user;
    }
// ----------------------------------------------

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@Validated @RequestBody LogoutRequestDTO req) {
        var accessToken = Claims.getJwtTokenString();
        req.setAccessToken(accessToken);
        return ApiResponse.success("Logout successful", logoutService.logoutUser(req));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh() {
        String tokenType = Claims.getTokenTypeFromJwt();
        if (!"REFRESH".equals(tokenType)) {
            return ApiResponse.failed(HttpStatus.UNAUTHORIZED.value(), "Invalid token type for refresh");
        }
        String token = Claims.getJwtTokenString();
        return ApiResponse.success("Refresh successful", tokenRefreshService.refreshAccessToken(token));
    }
}