package com.sdgs.itb.infrastructure.user.mapper;

import com.sdgs.itb.entity.user.*;
import com.sdgs.itb.infrastructure.user.dto.UserDTO;

import java.util.Set;
import java.util.stream.Collectors;

public class UserMapper {
    public static User toEntity(UserDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        return user;
    }

    public static UserDTO toUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());

        if (user.getUnit() != null) {
            dto.setUnitId(user.getUnit().getId());
            dto.setUnitName(user.getUnit().getName());
        }

        Set<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        dto.setRoles(roles);

        return dto;
    }
}
