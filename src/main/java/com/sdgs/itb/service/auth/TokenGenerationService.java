package com.sdgs.itb.service.auth;

import org.springframework.security.core.Authentication;

public interface TokenGenerationService {
    enum TokenType {
        ACCESS, REFRESH
    }

    String generateToken(Authentication authentication, TokenType tokenType);
    String refreshAccessToken(String refreshToken);
}
