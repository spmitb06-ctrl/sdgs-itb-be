package com.sdgs.itb.infrastructure.faculty.seed;

import com.sdgs.itb.entity.faculty.Faculty;
import com.sdgs.itb.infrastructure.faculty.repository.FacultyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FacultyDataSeeder implements CommandLineRunner {

    private final FacultyRepository facultyRepository;

    @Override
    public void run(String... args) {
        if (facultyRepository.count() == 0) {
            List<Faculty> faculties = List.of(
                    Faculty.builder().name("Faculty of Mathematics and Natural Sciences (FMIPA)").build(),
                    Faculty.builder().name("Faculty of Earth Sciences and Technology (FITB)").build(),
                    Faculty.builder().name("Faculty of Mining and Petroleum Engineering (FTTM)").build(),
                    Faculty.builder().name("Faculty of Industrial Technology (FTI)").build(),
                    Faculty.builder().name("Faculty of Mechanical and Aerospace Engineering (FTMD)").build(),
                    Faculty.builder().name("Faculty of Civil and Environmental Engineering (FTSL)").build(),
                    Faculty.builder().name("School of Electrical Engineering and Informatics (STEI)").build(),
                    Faculty.builder().name("Faculty of Art and Design (FSRD)").build(),
                    Faculty.builder().name("School of Architect and Regional Planning").build(),
                    Faculty.builder().name("School of Business and Management (SBM)").build(),
                    Faculty.builder().name("School of Pharmacy (SF)").build(),
                    Faculty.builder().name("School of Life Sciences and Technology (SITH)").build()
            );
            facultyRepository.saveAll(faculties);
            System.out.println("✅ Faculty data seeded successfully!");
        }
    }
}

