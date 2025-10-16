package com.sdgs.itb.service.goal.impl;

import com.sdgs.itb.entity.goal.Scholar;
import com.sdgs.itb.infrastructure.goal.dto.ScholarDTO;
import com.sdgs.itb.infrastructure.goal.mapper.ScholarMapper;
import com.sdgs.itb.infrastructure.goal.repository.ScholarRepository;
import com.sdgs.itb.service.goal.ScholarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScholarServiceImpl implements ScholarService {
    private final ScholarRepository scholarRepository;

    @Override
    public List<ScholarDTO> getAllScholars() {
        return scholarRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(ScholarMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ScholarDTO getScholar(Long id) {
        Scholar scholar = scholarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scholar not found"));
        return ScholarMapper.toDTO(scholar);
    }

    @Override
    public ScholarDTO createScholar(ScholarDTO dto) {
        Scholar scholar = ScholarMapper.toEntity(dto);

        return ScholarMapper.toDTO(scholarRepository.save(scholar));
    }

    @Override
    public ScholarDTO updateScholar(Long id, ScholarDTO dto) {
        Scholar scholar = scholarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scholar not found"));

        scholar.setName(dto.getName());
        scholar.setIconUrl(dto.getIconUrl());

        return ScholarMapper.toDTO(scholarRepository.save(scholar));
    }

    @Override
    public void deleteScholar(Long id) {
        Scholar scholar = scholarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scholar not found"));
        scholarRepository.deleteById(id);
    }
}

