package com.sdgs.itb.infrastructure.user.mapper;

import com.sdgs.itb.entity.user.*;
import com.sdgs.itb.infrastructure.user.dto.UserDTO;

import java.util.Set;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserDTO toUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setFaculty(user.getFaculty());
        dto.setDepartment(user.getDepartment());

        Set<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        dto.setRoles(roles);

        return dto;
    }
}
