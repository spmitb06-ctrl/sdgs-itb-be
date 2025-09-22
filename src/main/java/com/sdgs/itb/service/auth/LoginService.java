package com.sdgs.itb.service.auth;

import com.sdgs.itb.infrastructure.auth.dto.LoginRequestDTO;
import com.sdgs.itb.infrastructure.auth.dto.TokenPairResponseDTO;

public interface LoginService {
    TokenPairResponseDTO authenticateUser(LoginRequestDTO req);
}
