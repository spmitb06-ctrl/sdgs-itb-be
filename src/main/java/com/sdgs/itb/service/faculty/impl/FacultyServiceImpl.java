package com.sdgs.itb.service.faculty.impl;

import com.sdgs.itb.entity.faculty.Faculty;
import com.sdgs.itb.infrastructure.faculty.dto.FacultyDTO;
import com.sdgs.itb.infrastructure.faculty.mapper.FacultyMapper;
import com.sdgs.itb.infrastructure.faculty.repository.FacultyRepository;
import com.sdgs.itb.service.faculty.FacultyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacultyServiceImpl implements FacultyService {

    private final FacultyRepository facultyRepository;

    @Override
    public List<FacultyDTO> getAllFaculties() {
        return facultyRepository.findAll()
                .stream()
                .map(FacultyMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public FacultyDTO getFaculty(Long id) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));
        return FacultyMapper.toDTO(faculty);
    }
}

