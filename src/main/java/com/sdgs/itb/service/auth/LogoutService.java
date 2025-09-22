package com.sdgs.itb.service.auth;

import com.sdgs.itb.infrastructure.auth.dto.LogoutRequestDTO;

public interface LogoutService {
    Boolean logoutUser(LogoutRequestDTO req);
}
