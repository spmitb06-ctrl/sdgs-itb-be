package com.sdgs.itb.infrastructure.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenPairResponseDTO {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
}