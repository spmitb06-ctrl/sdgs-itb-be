package com.sdgs.itb.service.goal.impl;

import com.sdgs.itb.entity.goal.Scholar;
import com.sdgs.itb.infrastructure.goal.dto.ScholarDTO;
import com.sdgs.itb.infrastructure.goal.repository.ScholarRepository;
import com.sdgs.itb.service.goal.ScholarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScholarServiceImpl implements ScholarService {
    private final ScholarRepository scholarRepository;

    public List<ScholarDTO> getAllScholars() {
        return scholarRepository.findAll().stream()
                .map(s -> new ScholarDTO(s.getId(), s.getName()))
                .collect(Collectors.toList());
    }

    public ScholarDTO createScholar(ScholarDTO dto) {
        Scholar scholar = Scholar.builder().name(dto.getName()).build();
        scholarRepository.save(scholar);
        return new ScholarDTO(scholar.getId(), scholar.getName());
    }

    public ScholarDTO updateScholar(Long id, ScholarDTO dto) {
        Scholar scholar = scholarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scholar not found"));
        scholar.setName(dto.getName());
        scholarRepository.save(scholar);
        return new ScholarDTO(scholar.getId(), scholar.getName());
    }

    public void deleteScholar(Long id) {
        scholarRepository.deleteById(id);
    }
}

