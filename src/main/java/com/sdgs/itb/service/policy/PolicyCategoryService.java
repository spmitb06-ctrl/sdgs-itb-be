package com.sdgs.itb.service.policy;

import com.sdgs.itb.infrastructure.policy.dto.PolicyCategoryDTO;

import java.util.List;

public interface PolicyCategoryService {
//    PolicyCategoryDTO createPolicyCategory(PolicyCategoryDTO dto);
//    PolicyCategoryDTO updatePolicyCategory(Long id, PolicyCategoryDTO dto);
//    void deletePolicyCategory(Long id);
    List<PolicyCategoryDTO> getAllCategories();
    PolicyCategoryDTO getCategory(Long id);
}

