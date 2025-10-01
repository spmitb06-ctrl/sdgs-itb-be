package com.sdgs.itb.service.unit.impl;

import com.sdgs.itb.entity.unit.Unit;
import com.sdgs.itb.infrastructure.unit.dto.UnitDTO;
import com.sdgs.itb.infrastructure.unit.mapper.UnitMapper;
import com.sdgs.itb.infrastructure.unit.repository.UnitRepository;
import com.sdgs.itb.service.unit.UnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;

    @Override
    public List<UnitDTO> getAllUnits() {
        return unitRepository.findAll()
                .stream()
                .map(UnitMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UnitDTO getUnit(Long id) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unit not found"));
        return UnitMapper.toDTO(unit);
    }
}

