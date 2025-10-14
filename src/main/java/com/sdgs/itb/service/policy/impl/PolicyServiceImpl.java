package com.sdgs.itb.service.policy.impl;

import com.sdgs.itb.common.exceptions.DataNotFoundException;
import com.sdgs.itb.entity.goal.Goal;
import com.sdgs.itb.entity.policy.Policy;
import com.sdgs.itb.entity.policy.PolicyCategory;
import com.sdgs.itb.entity.policy.PolicyGoal;
import com.sdgs.itb.infrastructure.goal.repository.GoalRepository;
import com.sdgs.itb.infrastructure.policy.dto.PolicyDTO;
import com.sdgs.itb.infrastructure.policy.mapper.PolicyMapper;
import com.sdgs.itb.infrastructure.policy.repository.PolicyCategoryRepository;
import com.sdgs.itb.infrastructure.policy.repository.PolicyGoalRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicyServiceImpl implements PolicyService {

    private final PolicyRepository policyRepository;
    private final PolicyGoalRepository policyGoalRepository;
    private final PolicyCategoryRepository categoryRepository;
    private final GoalRepository goalRepository;

    @Override
    public PolicyDTO createPolicy(PolicyDTO dto) {
        Policy policy = PolicyMapper.toEntity(dto);

        PolicyCategory category = null;
        if (dto.getCategoryId() != null) {
            category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new DataNotFoundException("Category not found"));
            policy.setPolicyCategory(category);
        }

        String categoryName = category != null ? category.getCategory() : null;
        String imageUrl = dto.getImageUrl();
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            if ("ITB".equalsIgnoreCase(categoryName)) {
                imageUrl = "/policy/ITB.jpeg";
            } else if ("National".equalsIgnoreCase(categoryName)) {
                imageUrl = "/policy/National.jpg";
            } else if ("International".equalsIgnoreCase(categoryName)) {
                imageUrl = "/policy/International.jpg";
            } else {
                imageUrl = "/policy/Policy.jpg";
            }
        }
        policy.setImageUrl(imageUrl);

        Policy savedPolicy = policyRepository.saveAndFlush(policy);

        // Goals
        if (dto.getGoalIds() != null && !dto.getGoalIds().isEmpty()) {
            List<Goal> goals = goalRepository.findAllById(dto.getGoalIds());
            if (goals.isEmpty()) throw new DataNotFoundException("No valid goals found");

            for (Goal goal : goals) { // Regular for-loop
                PolicyGoal ag = PolicyGoal.builder()
                        .policy(savedPolicy)
                        .goal(goal)
                        .build();
                savedPolicy.getPolicyGoals().add(ag);
            }
        }

        return PolicyMapper.toDTO(policyRepository.save(policy));
    }

    @Transactional
    @Override
    public PolicyDTO updatePolicy(Long id, PolicyDTO dto) {
        Policy existing = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found"));

        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());
        existing.setFileUrl(dto.getFileUrl());
        existing.setYear(dto.getYear());

        PolicyCategory category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Category not found"));

        String categoryName = category.getCategory();
        String imageUrl = dto.getImageUrl();
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            if ("ITB".equalsIgnoreCase(categoryName)) {
                imageUrl = "/policy/ITB.jpeg";
            } else if ("National".equalsIgnoreCase(categoryName)) {
                imageUrl = "/policy/National.jpg";
            } else if ("International".equalsIgnoreCase(categoryName)) {
                imageUrl = "/policy/International.jpg";
            } else {
                imageUrl = "/policy/Policy.jpg";
            }
        }
        existing.setImageUrl(imageUrl);

        // Goals
        if (dto.getGoalIds() != null && !dto.getGoalIds().isEmpty()) {
            List<Goal> policyGoals = goalRepository.findAllById(dto.getGoalIds());
            if (policyGoals.isEmpty()) throw new DataNotFoundException("No valid goals found");

            // Fetch all current active PolicyGoal relations for this Policy
            List<PolicyGoal> existingPolicyGoals = policyGoalRepository.findAllByPolicyId(existing.getId());

            // Hard delete PolicyGoal records that are not in dto.getGoalIds()
            for (PolicyGoal oldPolicyGoal : existingPolicyGoals) {
                if (!dto.getGoalIds().contains(oldPolicyGoal.getGoal().getId())) {
                    policyGoalRepository.delete(oldPolicyGoal);
                }
            }

            // Remove goals that already exist (to avoid duplicates)
            policyGoals.removeIf(goal ->
                    policyGoalRepository.findByPolicyIdAndGoalId(existing.getId(), goal.getId()).isPresent()
            );

            // Add new PolicyGoal entries for remaining goals
            for (Goal goal : policyGoals) {
                PolicyGoal newPolicyGoal = PolicyGoal.builder()
                        .policy(existing)
                        .goal(goal)
                        .build();
                existing.getPolicyGoals().add(newPolicyGoal);
            }
        }

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
            List<Long> goalIds,
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
