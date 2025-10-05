package com.sdgs.itb.service.unit;

import com.sdgs.itb.infrastructure.unit.dto.UnitTypeDTO;

import java.util.List;

public interface UnitTypeService {
    List<UnitTypeDTO> getAllUnitTypes();
    UnitTypeDTO getUnitType(Long id);
}

