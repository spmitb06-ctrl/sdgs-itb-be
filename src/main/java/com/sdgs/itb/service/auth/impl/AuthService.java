package com.sdgs.itb.service.auth.impl;

import com.sdgs.itb.entity.user.Role;
import com.sdgs.itb.entity.user.User;
import com.sdgs.itb.common.exceptions.DataNotFoundException;
import com.sdgs.itb.infrastructure.user.repository.RoleRepository;
import com.sdgs.itb.infrastructure.user.repository.UserRepository;
import com.sdgs.itb.infrastructure.user.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return user;
    }

    @Transactional
    public User registerUser(User user, String roleName) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new DataNotFoundException("Role not found"));

        savedUser.addRole(role);
        return userRepository.save(savedUser);
    }

    @Transactional
    public void assignRoleToUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new DataNotFoundException("Role not found"));

        user.addRole(role);
        userRepository.save(user);
    }

    @Transactional
    public void removeRoleFromUser(Long userId, String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new DataNotFoundException("Role not found"));

        userRoleRepository.deleteByUserIdAndRoleId(userId, role.getId());
    }
}
