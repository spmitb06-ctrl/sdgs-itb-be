package com.sdgs.itb.service.user;

import com.sdgs.itb.entity.user.User;
import com.sdgs.itb.infrastructure.user.dto.EmailRequestDTO;
import com.sdgs.itb.infrastructure.user.dto.UserDTO;
import com.sdgs.itb.infrastructure.user.dto.UserProfileDTO;
import jakarta.mail.MessagingException;

public interface UserService {
    User requestRegistration(EmailRequestDTO req) throws MessagingException;
    User completeRegistration(String token, UserDTO req);

//    User registerUser(UserDTO userDTO) throws EmailAlreadyExistsException;
    User getUserById(Long id);
    User getUserByEmail(String email);
    UserProfileDTO updateUserProfile(Long userId, UserProfileDTO userProfileDTO);
}
