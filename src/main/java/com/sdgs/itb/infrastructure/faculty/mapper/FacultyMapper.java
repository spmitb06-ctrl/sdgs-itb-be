package com.sdgs.itb.infrastructure.faculty.mapper;

import com.sdgs.itb.entity.faculty.Faculty;
import com.sdgs.itb.infrastructure.faculty.dto.FacultyDTO;

public class FacultyMapper {

    public static FacultyDTO toDTO(Faculty faculty) {
        return FacultyDTO.builder()
                .id(faculty.getId())
                .name(faculty.getName())
                .build();
    }
}


