package com.sdgs.itb.infrastructure.user.mapper;

import com.sdgs.itb.entity.user.Role;
import com.sdgs.itb.infrastructure.user.dto.RoleDTO;

import java.util.Set;
import java.util.stream.Collectors;

public class RoleMapper {
    public static Role toEntity(RoleDTO dto) {
        Role role = new Role();
        role.setName(dto.getName());

        return role;
    }

    public static RoleDTO toRoleDTO(Role role) {
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());

        return dto;
    }
}
