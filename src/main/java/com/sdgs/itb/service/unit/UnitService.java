package com.sdgs.itb.service.unit;

import com.sdgs.itb.infrastructure.unit.dto.UnitDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UnitService {
    List<UnitDTO> getAllUnits();
    UnitDTO getUnit(Long id);
    Page<UnitDTO> getUnits(
            String name,
            List<Long> typeIds,
            String sortBy,
            String sortDir,
            int page,
            int size
    );
}

