package com.sdgs.itb.service.user.impl;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.sdgs.itb.common.exceptions.DataNotFoundException;
import com.sdgs.itb.common.exceptions.EmailAlreadyExistsException;
import com.sdgs.itb.common.exceptions.UnauthorizedException;
import com.sdgs.itb.common.utils.RandomGenerator;
import com.sdgs.itb.entity.unit.Unit;
import com.sdgs.itb.entity.user.*;
import com.sdgs.itb.infrastructure.auth.Claims;
import com.sdgs.itb.infrastructure.unit.repository.UnitRepository;
import com.sdgs.itb.infrastructure.user.dto.UserDTO;
import com.sdgs.itb.infrastructure.user.mapper.UserMapper;
import com.sdgs.itb.infrastructure.user.repository.RoleRepository;
import com.sdgs.itb.infrastructure.user.repository.UserRepository;
import com.sdgs.itb.service.user.AdminService;
import com.sdgs.itb.service.user.EmailService;
import com.sdgs.itb.service.user.specification.UserSpecification;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UnitRepository unitRepository;
    private final EmailService emailService;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    @Transactional
    public UserDTO registerUser(UserDTO req) throws EmailAlreadyExistsException, MessagingException {
        String userRole = Claims.getRoleFromJwt();
        if (userRole == null || !userRole.equals(RoleType.ADMIN_DEV) && !userRole.equals(RoleType.ADMIN_WCU)) {
            throw new UnauthorizedException("Only Web Admin can create new User!");
        }

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new EmailAlreadyExistsException("Email already in use");
        }

        User user = UserMapper.toEntity(req);

//        String rawPassword = RandomGenerator.generateRandomPassword(12);
        user.setPassword(passwordEncoder.encode(req.getPassword()));

        if (req.getUnitId() != null) {
            Unit unit = unitRepository.findById(req.getUnitId())
                    .orElseThrow(() -> new DataNotFoundException("Unit not found"));
            user.setUnit(unit);
        }

        // Save user (needed for ID)
        User savedUser = userRepository.save(user);

        // Assign roles
        for (String roleName : req.getRoles()) {
            roleRepository.findByName(roleName)
                    .ifPresentOrElse(
                            savedUser::addRole,
                            () -> { throw new IllegalArgumentException("Role not found: " + roleName); }
                    );
        }

//        String token = UUID.randomUUID().toString();
//        user.setVerificationToken(token);
//        user.setVerificationTokenExpiry(OffsetDateTime.now().plusHours(24));
//
//        String link = frontendUrl + "/verify?verificationToken=" + token;
//        emailService.sendVerificationEmail(req.getEmail(), link);

        // Comment this code when using the mail again
        user.setEmailVerified(true);

        savedUser = userRepository.save(savedUser);
        return UserMapper.toUserDTO(savedUser);
    }

    @Override
    @Transactional
    public User completeRegistration(String token, UserDTO req) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (user.getVerificationTokenExpiry().isBefore(OffsetDateTime.now())) {
            throw new TokenExpiredException("Token expired!");
        }

        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserDTO dto) throws MessagingException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        // update name (always allowed)
        if (dto.getName() != null) {
            user.setName(dto.getName());
        }

        // update unit (only ADMIN_DEV / ADMIN_WCU)
        String requesterRole = Claims.getRoleFromJwt();
        if (dto.getUnitId() != null) {
            if (!RoleType.ADMIN_DEV.equals(requesterRole) && !RoleType.ADMIN_WCU.equals(requesterRole)) {
                throw new UnauthorizedException("Only ADMIN_DEV or ADMIN_WCU can change unit");
            }
            Unit unit = unitRepository.findById(dto.getUnitId())
                    .orElseThrow(() -> new DataNotFoundException("Unit not found"));
            user.setUnit(unit);
        }

        // update email (requires re-verification)
        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new EmailAlreadyExistsException("Email already in use");
            }
            String token = UUID.randomUUID().toString();
            user.setVerificationToken(token);
            user.setVerificationTokenExpiry(OffsetDateTime.now().plusHours(24));

            String link = frontendUrl + "/verify-email-change?token=" + token + "&newEmail=" + dto.getEmail();
            emailService.sendVerificationEmail(dto.getEmail(), link);
        }

        // update roles (only ADMIN_DEV / ADMIN_WCU)
        if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
            if (!RoleType.ADMIN_DEV.equals(requesterRole) && !RoleType.ADMIN_WCU.equals(requesterRole)) {
                throw new UnauthorizedException("Only ADMIN_DEV or ADMIN_WCU can change roles");
            }

            // clear existing roles
            user.getUserRoles().clear();

            // assign new roles
            for (String roleName : dto.getRoles()) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new DataNotFoundException("Role not found: " + roleName));
                user.addRole(role);
            }
        }

        return UserMapper.toUserDTO(userRepository.save(user));
    }

    @Transactional
    public void confirmEmailChange(String token, String newEmail) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        if (user.getVerificationTokenExpiry().isBefore(OffsetDateTime.now())) {
            throw new TokenExpiredException("Token expired!");
        }

        user.setEmail(newEmail);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);
    }

    @Transactional
    public void requestPasswordChange(Long userId) throws MessagingException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setVerificationTokenExpiry(OffsetDateTime.now().plusHours(1));

        String link = frontendUrl + "/change-password?token=" + token;
        emailService.sendPasswordChangeEmail(user.getEmail(), link);
        userRepository.save(user);
    }

    @Transactional
    public void changePassword(String token, String newPassword) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        if (user.getVerificationTokenExpiry().isBefore(OffsetDateTime.now())) {
            throw new TokenExpiredException("Token expired!");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);
    }

    @Transactional
    public void forgotPassword(String email) throws MessagingException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setVerificationTokenExpiry(OffsetDateTime.now().plusHours(1));

        String link = frontendUrl + "/reset-password?token=" + token;
        emailService.sendPasswordResetEmail(email, link);

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        userRepository.delete(user);
    }

    @Override
    public UserDTO getUser(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toUserDTO)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
    }

    @Override
    public Page<UserDTO> getUsers(
            List<Long> roleIds,
            List<Long> unitIds,
            String sortBy,
            String sortDir,
            int page,
            int size
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<User> spec = Specification
                .where(UserSpecification.notDeleted())
                .and(UserSpecification.hasRoles(roleIds))
                .and(UserSpecification.hasUnits(unitIds));

        return userRepository.findAll(spec, pageable).map(UserMapper::toUserDTO);
    }
}
