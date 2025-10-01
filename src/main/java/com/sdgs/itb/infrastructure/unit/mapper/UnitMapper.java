package com.sdgs.itb.infrastructure.unit.mapper;

import com.sdgs.itb.entity.unit.Unit;
import com.sdgs.itb.infrastructure.unit.dto.UnitDTO;

public class UnitMapper {

    public static UnitDTO toDTO(Unit unit) {
        return UnitDTO.builder()
                .id(unit.getId())
                .name(unit.getName())
                .build();
    }
}


