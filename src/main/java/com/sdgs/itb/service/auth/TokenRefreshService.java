package com.sdgs.itb.service.auth;

import com.sdgs.itb.infrastructure.auth.dto.TokenPairResponseDTO;

public interface TokenRefreshService {
    TokenPairResponseDTO refreshAccessToken(String refreshToken);
}
