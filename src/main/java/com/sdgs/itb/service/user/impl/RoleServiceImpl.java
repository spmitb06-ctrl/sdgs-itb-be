package com.sdgs.itb.service.user.impl;

import com.sdgs.itb.infrastructure.user.dto.RoleDTO;
import com.sdgs.itb.infrastructure.user.mapper.RoleMapper;
import com.sdgs.itb.infrastructure.user.repository.RoleRepository;
import com.sdgs.itb.service.user.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public List<RoleDTO> getRoles() {
        return roleRepository.findAll()
                .stream()
                .map(RoleMapper::toRoleDTO)
                .collect(Collectors.toList());
    }
}
