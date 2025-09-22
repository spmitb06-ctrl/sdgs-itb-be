package com.sdgs.itb.infrastructure.user.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UserDTO {
    private Long id;
    private String email;
    private String password;
    private String name;
    private Set<String> roles;
    private String faculty;
    private String department;
}
