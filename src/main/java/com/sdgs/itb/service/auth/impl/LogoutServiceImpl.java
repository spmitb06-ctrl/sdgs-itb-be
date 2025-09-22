package com.sdgs.itb.service.auth.impl;

import com.sdgs.itb.infrastructure.auth.dto.LogoutRequestDTO;
import com.sdgs.itb.service.auth.LogoutService;
import com.sdgs.itb.service.auth.TokenBlacklistService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class LogoutServiceImpl implements LogoutService {
    private final JwtDecoder jwtDecoder;
    private final TokenBlacklistService TokenBlacklistService;


    public LogoutServiceImpl(
            JwtDecoder jwtDecoder,
            TokenBlacklistService tokenBlacklistUsecase
    ) {
        this.jwtDecoder = jwtDecoder;
        TokenBlacklistService = tokenBlacklistUsecase;
    }

    @Override
    public Boolean logoutUser(LogoutRequestDTO req) {
        Jwt accessToken = jwtDecoder.decode(req.getAccessToken());
        Jwt refreshToken = jwtDecoder.decode(req.getRefreshToken());

        TokenBlacklistService.blacklistToken(accessToken.getTokenValue(), Objects.requireNonNull(accessToken.getExpiresAt()).toString());
        TokenBlacklistService.blacklistToken(refreshToken.getTokenValue(), Objects.requireNonNull(refreshToken.getExpiresAt()).toString());
        return Boolean.TRUE;
    }
}
