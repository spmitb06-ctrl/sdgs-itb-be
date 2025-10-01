package com.sdgs.itb.infrastructure.goal.mapper;

import com.sdgs.itb.entity.goal.Goal;
import com.sdgs.itb.infrastructure.goal.dto.GoalDTO;
import com.sdgs.itb.infrastructure.goal.dto.GoalScholarDTO;

import java.util.stream.Collectors;

public class GoalMapper {

    public static GoalDTO toDTO(Goal goal) {
        return GoalDTO.builder()
                .id(goal.getId())
                .goalNumber(goal.getGoalNumber())
                .title(goal.getTitle())
                .description(goal.getDescription())
                .color(goal.getColor())
                .icon(goal.getIcon())
                .editedIcon(goal.getEditedIcon())
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
                .goalNumber(dto.getGoalNumber())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .color(dto.getColor())
                .icon(dto.getIcon())
                .editedIcon(dto.getEditedIcon())
                .linkUrl(dto.getLinkUrl())
                .build();
    }
}


