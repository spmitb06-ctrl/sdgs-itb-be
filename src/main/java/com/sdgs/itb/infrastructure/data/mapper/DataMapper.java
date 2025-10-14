package com.sdgs.itb.infrastructure.data.mapper;

import com.sdgs.itb.entity.data.*;
import com.sdgs.itb.entity.goal.Goal;
import com.sdgs.itb.entity.unit.Unit;
import com.sdgs.itb.infrastructure.data.dto.*;
import com.sdgs.itb.infrastructure.goal.dto.GoalDTO;
import com.sdgs.itb.infrastructure.unit.dto.UnitDTO;

import java.util.stream.Collectors;

public class DataMapper {
    public static Data toEntity(DataDTO dto) {
        Data data = new Data();
        data.setTitle(dto.getTitle());
        data.setContent(dto.getContent());
        data.setThumbnailUrl(dto.getThumbnailUrl());
        data.setSourceUrl(dto.getSourceUrl());
        data.setEventDate(dto.getEventDate());

        return data;
    }

    public static DataDTO toDTO(Data data) {
        DataDTO dto = new DataDTO();
        dto.setId(data.getId());
        dto.setTitle(data.getTitle());
        dto.setContent(data.getContent());
        dto.setThumbnailUrl(data.getThumbnailUrl());
        dto.setSourceUrl(data.getSourceUrl());
        dto.setEventDate(data.getEventDate());

        dto.setDataImages(data.getDataImages().stream()
                .map(DataMapper::toDataImageDTO)
                .collect(Collectors.toList()));

        dto.setDataUnits(data.getDataUnits().stream()
                .map(DataMapper::toDataUnitDTO)
                .collect(Collectors.toList()));

        dto.setDataGoals(data.getDataGoals().stream()
                .map(DataMapper::toDataGoalDTO)
                .collect(Collectors.toList()));

        if (data.getDataCategory() != null) {
            dto.setCategoryId(data.getDataCategory().getId());
            dto.setCategoryName(data.getDataCategory().getCategory());
        }

        return dto;
    }

    public static UnitDTO toUnitDTO(Unit unit) {
        UnitDTO dto = new UnitDTO();
        dto.setId(unit.getId());
        dto.setName(unit.getName());
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

    public static DataImageDTO toDataImageDTO(DataImage dataImage) {
        DataImageDTO dto = new DataImageDTO();
        dto.setId(dataImage.getId());
        dto.setImageUrl(dataImage.getImageUrl());
        return dto;
    }

    public static DataUnitDTO toDataUnitDTO(DataUnit dataUnit) {
        DataUnitDTO dto = new DataUnitDTO();
        dto.setId(dataUnit.getId());
        dto.setUnits(toUnitDTO(dataUnit.getUnit()));
        return dto;
    }

    public static DataGoalDTO toDataGoalDTO(DataGoal dataGoal) {
        DataGoalDTO dto = new DataGoalDTO();
        dto.setId(dataGoal.getId());
        dto.setGoals(toGoalDTO(dataGoal.getGoal()));
        return dto;
    }
}

