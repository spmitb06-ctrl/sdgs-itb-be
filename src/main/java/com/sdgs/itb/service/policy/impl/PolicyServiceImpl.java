package com.sdgs.itb.service.policy.impl;

import com.sdgs.itb.common.exceptions.DataNotFoundException;
import com.sdgs.itb.entity.policy.Policy;
import com.sdgs.itb.entity.policy.PolicyCategory;
import com.sdgs.itb.infrastructure.policy.dto.PolicyDTO;
import com.sdgs.itb.infrastructure.policy.mapper.PolicyMapper;
import com.sdgs.itb.infrastructure.policy.repository.PolicyCategoryRepository;
import com.sdgs.itb.infrastructure.policy.repository.PolicyRepository;
import com.sdgs.itb.service.policy.PolicyService;
import com.sdgs.itb.service.policy.specification.PolicySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicyServiceImpl implements PolicyService {

    private final PolicyRepository policyRepository;
    private final PolicyCategoryRepository categoryRepository;

    @Override
    public PolicyDTO createPolicy(PolicyDTO dto) {
        Policy policy = PolicyMapper.toEntity(dto);

        if (dto.getCategoryId() != null) {
            PolicyCategory category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new DataNotFoundException("Category not found"));
            policy.setPolicyCategory(category);
        }

        return PolicyMapper.toDTO(policyRepository.save(policy));
    }

    @Override
    public PolicyDTO updatePolicy(Long id, PolicyDTO dto) {
        Policy existing = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found"));

        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());
        existing.setFileUrl(dto.getFileUrl());
        existing.setYear(dto.getYear());

        return PolicyMapper.toDTO(policyRepository.save(existing));
    }

    @Override
    public void deletePolicy(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found"));

        policy.setDeletedAt(java.time.OffsetDateTime.now());

        policyRepository.save(policy);
    }

    @Override
    public PolicyDTO getPolicy(Long id) {
        return policyRepository.findById(id)
                .filter(policy -> policy.getDeletedAt() == null)
                .map(PolicyMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Policy not found"));
    }

    @Override
    public Page<PolicyDTO> getPolicies(
            String title,
            String year,
            List<Long> categoryIds,
            String sortBy,
            String sortDir,
            int page,
            int size
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Policy> spec = Specification
                .where(PolicySpecification.notDeleted())
                .and(PolicySpecification.hasTitle(title))
                .and(PolicySpecification.hasYear(year))
                .and(PolicySpecification.hasCategories(categoryIds));

        return policyRepository.findAll(spec, pageable).map(PolicyMapper::toDTO);
    }
}
