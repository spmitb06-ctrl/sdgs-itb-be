package com.sdgs.itb.infrastructure.user.controller;

import com.sdgs.itb.common.responses.ApiResponse;
import com.sdgs.itb.entity.user.User;
import com.sdgs.itb.infrastructure.auth.Claims;
import com.sdgs.itb.infrastructure.user.dto.EmailRequestDTO;
import com.sdgs.itb.infrastructure.user.dto.UserDTO;
import com.sdgs.itb.infrastructure.user.dto.UserProfileDTO;
import com.sdgs.itb.service.user.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/register/request")
    public ResponseEntity<?> requestRegistration(@Valid @RequestBody EmailRequestDTO req) throws MessagingException {
        return ApiResponse.success(HttpStatus.OK.value(), "Verification email sent!", userService.requestRegistration(req));
    }

//    @PostMapping("/register")
//    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO userDTO) {
//        try {
//            return ApiResponse.success(HttpStatus.OK.value(), "Register user success!", userService.registerUser(userDTO));
//        } catch (EmailAlreadyExistsException e) {
//            return ApiResponse.failed(e.getMessage());
//        }
//    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        Long userId = Claims.getUserIdFromJwt();
        User user = userService.getUserById(userId);

        if (user == null) {
            return ApiResponse.failed(HttpStatus.NOT_FOUND.value(), "User not found");
        }

        UserProfileDTO dto = UserProfileDTO.fromEntity(user);
        return ApiResponse.success(HttpStatus.OK.value(), "User profile fetched successfully", dto);
    }

    @PutMapping("/register/complete")
    public ResponseEntity<?> completeRegistration(
            @RequestParam String verificationToken,
            @RequestBody @Valid UserDTO req) {
        return ApiResponse.success(HttpStatus.OK.value(), "Account created!", userService.completeRegistration(verificationToken, req));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody @Valid UserProfileDTO userProfileDTO) {
        Long userId = Claims.getUserIdFromJwt();
        User user = userService.getUserById(userId);

        if (user == null) {
            return ApiResponse.failed(HttpStatus.NOT_FOUND.value(), "User not found");
        }

        UserProfileDTO updatedUser = userService.updateUserProfile(userId, userProfileDTO);
        return ApiResponse.success(HttpStatus.OK.value(), "User profile updated successfully", updatedUser);
    }
}
