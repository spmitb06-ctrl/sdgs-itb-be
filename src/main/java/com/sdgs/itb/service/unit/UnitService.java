package com.sdgs.itb.service.unit;

import com.sdgs.itb.infrastructure.unit.dto.UnitDTO;

import java.util.List;

public interface UnitService {
    List<UnitDTO> getAllUnits();
    UnitDTO getUnit(Long id);
}

