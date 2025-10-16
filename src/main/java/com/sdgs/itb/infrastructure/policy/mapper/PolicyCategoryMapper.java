package com.sdgs.itb.infrastructure.policy.mapper;

import com.sdgs.itb.entity.policy.PolicyCategory;
import com.sdgs.itb.infrastructure.policy.dto.PolicyCategoryDTO;

public class PolicyCategoryMapper {
    public static PolicyCategory toEntity(PolicyCategoryDTO dto) {
        PolicyCategory policyCategory = new PolicyCategory();
        policyCategory.setCategory(dto.getCategory());
        policyCategory.setColor(dto.getColor());
        policyCategory.setIconUrl(dto.getIconUrl());

        return policyCategory;
    }

    public static PolicyCategoryDTO toDTO(PolicyCategory policyCategory) {
        return PolicyCategoryDTO.builder()
                .id(policyCategory.getId())
                .category(policyCategory.getCategory())
                .color((policyCategory.getColor()))
                .iconUrl(policyCategory.getIconUrl())
                .build();
    }
}


