package com.sdgs.itb.infrastructure.unit.mapper;

import com.sdgs.itb.entity.unit.UnitType;
import com.sdgs.itb.infrastructure.unit.dto.UnitTypeDTO;

public class UnitTypeMapper {

    public static UnitTypeDTO toDTO(UnitType unitType) {
        return UnitTypeDTO.builder()
                .id(unitType.getId())
                .name(unitType.getName())
                .build();
    }
}


