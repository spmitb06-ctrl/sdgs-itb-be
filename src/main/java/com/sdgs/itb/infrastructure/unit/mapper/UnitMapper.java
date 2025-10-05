package com.sdgs.itb.infrastructure.unit.mapper;

import com.sdgs.itb.entity.unit.Unit;
import com.sdgs.itb.infrastructure.unit.dto.UnitDTO;

public class UnitMapper {
    public static Unit toEntity(UnitDTO dto) {
        Unit unit = new Unit();
        unit.setOrganizationId(dto.getOrganizationId());
        unit.setName(dto.getName());

        return unit;
    }

    public static UnitDTO toDTO(Unit unit) {
        UnitDTO dto = new UnitDTO();
        dto.setId(unit.getId());
        dto.setOrganizationId(unit.getOrganizationId());
        dto.setName(unit.getName());

        if (unit.getUnitType() != null) {
            dto.setTypeId(unit.getUnitType().getId());
            dto.setTypeName(unit.getUnitType().getName());
        }

        return dto;
    }
}


