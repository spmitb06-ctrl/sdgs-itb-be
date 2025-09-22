package com.sdgs.itb.infrastructure.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailRequestDTO {
    @Email
    @NotBlank
    private String email;
}
