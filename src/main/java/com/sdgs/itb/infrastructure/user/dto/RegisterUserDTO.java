package com.sdgs.itb.infrastructure.user.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class RegisterUserDTO {
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Role is required")
    private Set<String> roles;

    @NotBlank(message = "Unit is required")
    private String unit;
}
