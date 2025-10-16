package com.sdgs.itb.infrastructure.goal.mapper;

import com.sdgs.itb.entity.goal.Scholar;
import com.sdgs.itb.infrastructure.goal.dto.ScholarDTO;

public class ScholarMapper {

    public static ScholarDTO toDTO(Scholar scholar) {
        return ScholarDTO.builder()
                .id(scholar.getId())
                .name(scholar.getName())
                .iconUrl(scholar.getIconUrl())
                .build();
    }

    public static Scholar toEntity(ScholarDTO dto) {
        return Scholar.builder()
                .id(dto.getId())
                .name(dto.getName())
                .iconUrl(dto.getIconUrl())
                .build();
    }
}


