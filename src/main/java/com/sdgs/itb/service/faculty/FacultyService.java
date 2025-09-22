package com.sdgs.itb.service.faculty;

import com.sdgs.itb.infrastructure.faculty.dto.FacultyDTO;

import java.util.List;

public interface FacultyService {
    List<FacultyDTO> getAllFaculties();
    FacultyDTO getFaculty(Long id);
}

