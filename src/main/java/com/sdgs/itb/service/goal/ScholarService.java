package com.sdgs.itb.service.goal;

import com.sdgs.itb.infrastructure.goal.dto.ScholarDTO;

import java.util.List;

public interface ScholarService {
    List<ScholarDTO> getAllScholars();
    ScholarDTO createScholar(ScholarDTO dto);
    ScholarDTO updateScholar(Long id, ScholarDTO dto);
    void deleteScholar(Long id);
}

