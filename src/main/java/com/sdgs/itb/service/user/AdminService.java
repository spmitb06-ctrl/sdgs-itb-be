package com.sdgs.itb.service.user;

import com.sdgs.itb.common.exceptions.EmailAlreadyExistsException;
import com.sdgs.itb.infrastructure.user.dto.UserDTO;

public interface AdminService {
    UserDTO registerUser(UserDTO userDTO) throws EmailAlreadyExistsException;
}
