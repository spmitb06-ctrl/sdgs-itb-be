package com.sdgs.itb.service.user.impl;

import com.sdgs.itb.common.exceptions.EmailAlreadyExistsException;
import com.sdgs.itb.common.exceptions.UnauthorizedException;
import com.sdgs.itb.entity.user.*;
import com.sdgs.itb.infrastructure.auth.Claims;
import com.sdgs.itb.infrastructure.user.dto.UserDTO;
import com.sdgs.itb.infrastructure.user.mapper.UserMapper;
import com.sdgs.itb.infrastructure.user.repository.RoleRepository;
import com.sdgs.itb.infrastructure.user.repository.UserRepository;
import com.sdgs.itb.service.user.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDTO registerUser(UserDTO userDTO) throws EmailAlreadyExistsException {
        String userRole = Claims.getRoleFromJwt();
        if (userRole == null || !userRole.equals(RoleType.ADMIN_WEB)) {
            throw new UnauthorizedException("Only Web Admin can create new User!");
        }

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new EmailAlreadyExistsException("Email already in use");
        }

        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setName(userDTO.getName());
        user.setFaculty(userDTO.getFaculty());
        user.setDepartment(userDTO.getDepartment());

        // Save user (needed for ID)
        User savedUser = userRepository.save(user);

        // Assign roles
        for (String roleName : userDTO.getRoles()) {
            roleRepository.findByName(roleName)
                    .ifPresentOrElse(
                            savedUser::addRole,
                            () -> { throw new IllegalArgumentException("Role not found: " + roleName); }
                    );
        }


        savedUser = userRepository.save(savedUser);
        return UserMapper.toUserDTO(savedUser);
    }
}
