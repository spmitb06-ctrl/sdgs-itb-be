package com.sdgs.itb.service.unit.impl;

import com.sdgs.itb.entity.unit.Unit;
import com.sdgs.itb.infrastructure.unit.dto.UnitDTO;
import com.sdgs.itb.infrastructure.unit.mapper.UnitMapper;
import com.sdgs.itb.infrastructure.unit.repository.UnitRepository;
import com.sdgs.itb.service.unit.UnitService;
import com.sdgs.itb.service.unit.specification.UnitSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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

    @Override
    public Page<UnitDTO> getUnits(
            String name,
            List<Long> typeIds,
            String sortBy,
            String sortDir,
            int page,
            int size
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Unit> spec = Specification
                .where(UnitSpecification.notDeleted())
                .and(UnitSpecification.hasName(name))
                .and(UnitSpecification.hasTypes(typeIds));

        return unitRepository.findAll(spec, pageable).map(UnitMapper::toDTO);
    }
}

