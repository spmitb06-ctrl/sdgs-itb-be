package com.sdgs.itb.infrastructure.policy.mapper;

import com.sdgs.itb.entity.policy.Policy;
import com.sdgs.itb.infrastructure.policy.dto.PolicyDTO;

public class PolicyMapper {
    public static Policy toEntity(PolicyDTO dto) {
        Policy policy = new Policy();
        policy.setTitle(dto.getTitle());
        policy.setDescription(dto.getDescription());
        policy.setFileUrl(dto.getFileUrl());
        policy.setYear(dto.getYear());

        return policy;
    }

    public static PolicyDTO toDTO(Policy policy) {
        PolicyDTO dto = new PolicyDTO();
        dto.setId(policy.getId());
        dto.setTitle(policy.getTitle());
        dto.setDescription(policy.getDescription());
        dto.setFileUrl(policy.getFileUrl());
        dto.setYear(policy.getYear());

        if (policy.getPolicyCategory() != null) {
            dto.setCategoryId(policy.getPolicyCategory().getId());
            dto.setCategoryName(policy.getPolicyCategory().getCategory());
        }

        return dto;
    }
}

