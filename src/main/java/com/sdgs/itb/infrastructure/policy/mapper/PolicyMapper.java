package com.sdgs.itb.infrastructure.policy.mapper;

import com.sdgs.itb.entity.goal.Goal;
import com.sdgs.itb.entity.news.NewsGoal;
import com.sdgs.itb.entity.policy.Policy;
import com.sdgs.itb.entity.policy.PolicyGoal;
import com.sdgs.itb.infrastructure.goal.dto.GoalDTO;
import com.sdgs.itb.infrastructure.news.dto.NewsGoalDTO;
import com.sdgs.itb.infrastructure.policy.dto.PolicyDTO;
import com.sdgs.itb.infrastructure.policy.dto.PolicyGoalDTO;

import java.util.stream.Collectors;

public class PolicyMapper {
    public static Policy toEntity(PolicyDTO dto) {
        Policy policy = new Policy();
        policy.setTitle(dto.getTitle());
        policy.setDescription(dto.getDescription());
        policy.setFileUrl(dto.getFileUrl());
        policy.setYear(dto.getYear());
        policy.setImageUrl(dto.getImageUrl());
        policy.setSourceUrl(dto.getSourceUrl());

        return policy;
    }

    public static PolicyDTO toDTO(Policy policy) {
        PolicyDTO dto = new PolicyDTO();
        dto.setId(policy.getId());
        dto.setTitle(policy.getTitle());
        dto.setDescription(policy.getDescription());
        dto.setFileUrl(policy.getFileUrl());
        dto.setYear(policy.getYear());
        dto.setImageUrl(policy.getImageUrl());
        dto.setSourceUrl(policy.getSourceUrl());

        dto.setPolicyGoals(policy.getPolicyGoals().stream()
                .map(PolicyMapper::toPolicyGoalDTO)
                .collect(Collectors.toList()));

        if (policy.getPolicyCategory() != null) {
            dto.setCategoryId(policy.getPolicyCategory().getId());
            dto.setCategoryName(policy.getPolicyCategory().getCategory());
            dto.setCategoryColor(policy.getPolicyCategory().getColor());
        }

        return dto;
    }

    public static GoalDTO toGoalDTO(Goal goal) {
        GoalDTO dto = new GoalDTO();
        dto.setId(goal.getId());
        dto.setGoalNumber(goal.getGoalNumber());
        dto.setTitle(goal.getTitle());
        dto.setDescription(goal.getDescription());
        dto.setColor(goal.getColor());
        dto.setIcon(goal.getIcon());
        dto.setEditedIcon(goal.getEditedIcon());
        dto.setLinkUrl(goal.getLinkUrl());
        return dto;
    }

    public static PolicyGoalDTO toPolicyGoalDTO(PolicyGoal policyGoal) {
        PolicyGoalDTO dto = new PolicyGoalDTO();
        dto.setId(policyGoal.getId());
        dto.setGoals(toGoalDTO(policyGoal.getGoal()));
        return dto;
    }
}

