package com.sdgs.itb.infrastructure.user.dto;

import com.sdgs.itb.entity.user.*;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class UserProfileDTO {
    private Long id;
    private String email;
    private String name;
    private Set<String> roles;
    private String unit;
    private String department;

    public static UserProfileDTO fromEntity(User user) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());

        dto.setRoles(
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet())
        );

        return dto;
    }
}
