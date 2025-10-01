package com.sdgs.itb.service.user;

import com.sdgs.itb.common.exceptions.EmailAlreadyExistsException;
import com.sdgs.itb.entity.user.User;
import com.sdgs.itb.infrastructure.user.dto.UserDTO;
import jakarta.mail.MessagingException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AdminService {
    UserDTO registerUser(UserDTO userDTO) throws EmailAlreadyExistsException, MessagingException;
    User completeRegistration(String token, UserDTO req);
    UserDTO updateUser(Long id, UserDTO dto) throws MessagingException;
    void confirmEmailChange(String token, String newEmail);
    void requestPasswordChange(Long userId) throws MessagingException;
    void deleteUser(Long id);
    void changePassword(String token, String newPassword);
    void forgotPassword(String email) throws MessagingException;
    UserDTO getUser(Long id);
    Page<UserDTO> getUsers(
            List<Long> roleIds,
            List<Long> unitIds,
            String sortBy,
            String sortDir,
            int page,
            int size
    );
}
