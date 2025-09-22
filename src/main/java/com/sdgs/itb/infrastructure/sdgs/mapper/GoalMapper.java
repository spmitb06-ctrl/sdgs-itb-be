package com.sdgs.itb.infrastructure.sdgs.mapper;

import com.sdgs.itb.entity.sdgs.Goal;
import com.sdgs.itb.infrastructure.sdgs.dto.GoalDTO;
import com.sdgs.itb.infrastructure.sdgs.dto.GoalScholarDTO;

import java.util.stream.Collectors;

public class GoalMapper {

    public static GoalDTO toDTO(Goal goal) {
        return GoalDTO.builder()
                .id(goal.getId())
                .title(goal.getTitle())
                .description(goal.getDescription())
                .color(goal.getColor())
                .icon(goal.getIcon())
                .linkUrl(goal.getLinkUrl())
                .scholars(goal.getGoalScholars() == null ? null :
                        goal.getGoalScholars().stream().map(gs -> GoalScholarDTO.builder()
                                        .scholarId(gs.getScholar().getId())
                                        .scholarName(gs.getScholar().getName())
                                        .link(gs.getLink())
                                        .build())
                                .collect(Collectors.toList()))
                .build();
    }

    public static Goal toEntity(GoalDTO dto) {
        return Goal.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .color(dto.getColor())
                .icon(dto.getIcon())
                .linkUrl(dto.getLinkUrl())
                .build();
    }
}


