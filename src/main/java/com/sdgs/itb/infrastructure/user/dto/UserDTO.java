package com.sdgs.itb.infrastructure.user.dto;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class UserDTO {
    private Long id;
    private String email;
    private String password;
    private String name;
    private Set<String> roles;
    private Long unitId;
    private String unitName;

    private List<Long> roleIds;
    private List<Long> unitIds;
}
