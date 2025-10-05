package com.sdgs.itb.service.unit.impl;

import com.sdgs.itb.entity.unit.UnitType;
import com.sdgs.itb.infrastructure.unit.dto.UnitTypeDTO;
import com.sdgs.itb.infrastructure.unit.mapper.UnitTypeMapper;
import com.sdgs.itb.infrastructure.unit.repository.UnitTypeRepository;
import com.sdgs.itb.service.unit.UnitTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnitTypeServiceImpl implements UnitTypeService {

    private final UnitTypeRepository unitTypeRepository;

    @Override
    public List<UnitTypeDTO> getAllUnitTypes() {
        return unitTypeRepository.findAll()
                .stream()
                .map(UnitTypeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UnitTypeDTO getUnitType(Long id) {
        UnitType unitType = unitTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unit type not found"));
        return UnitTypeMapper.toDTO(unitType);
    }
}
