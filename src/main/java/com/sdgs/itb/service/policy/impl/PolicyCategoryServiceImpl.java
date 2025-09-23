package com.sdgs.itb.service.policy.impl;

import com.sdgs.itb.entity.policy.PolicyCategory;
import com.sdgs.itb.infrastructure.policy.dto.PolicyCategoryDTO;
import com.sdgs.itb.infrastructure.policy.mapper.PolicyCategoryMapper;
import com.sdgs.itb.infrastructure.policy.repository.PolicyCategoryRepository;
import com.sdgs.itb.service.policy.PolicyCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PolicyCategoryServiceImpl implements PolicyCategoryService {

    private final PolicyCategoryRepository policyCategoryRepository;

    @Override
    public List<PolicyCategoryDTO> getAllCategories() {
        return policyCategoryRepository.findAll()
                .stream()
                .map(PolicyCategoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PolicyCategoryDTO getCategory(Long id) {
        PolicyCategory policyCategory = policyCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy category not found"));
        return PolicyCategoryMapper.toDTO((policyCategory));
    }
}
