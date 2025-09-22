package com.sdgs.itb.infrastructure.auth.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotNull
    @Size(max = 50)
    private String email;

    @NotNull
    private String password;
}