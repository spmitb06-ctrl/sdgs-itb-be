package com.sdgs.itb.infrastructure.faculty.repository;

import com.sdgs.itb.entity.faculty.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
}


