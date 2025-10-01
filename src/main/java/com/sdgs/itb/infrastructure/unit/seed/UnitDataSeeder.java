package com.sdgs.itb.infrastructure.unit.seed;

import com.sdgs.itb.entity.unit.Unit;
import com.sdgs.itb.infrastructure.unit.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UnitDataSeeder implements CommandLineRunner {

    private final UnitRepository unitRepository;

    @Override
    public void run(String... args) {
        if (unitRepository.count() == 0) {
            List<Unit> units = List.of(
                    Unit.builder().name("Unit of Mathematics and Natural Sciences (FMIPA)").build(),
                    Unit.builder().name("Unit of Earth Sciences and Technology (FITB)").build(),
                    Unit.builder().name("Unit of Mining and Petroleum Engineering (FTTM)").build(),
                    Unit.builder().name("Unit of Industrial Technology (FTI)").build(),
                    Unit.builder().name("Unit of Mechanical and Aerospace Engineering (FTMD)").build(),
                    Unit.builder().name("Unit of Civil and Environmental Engineering (FTSL)").build(),
                    Unit.builder().name("School of Electrical Engineering and Informatics (STEI)").build(),
                    Unit.builder().name("Unit of Art and Design (FSRD)").build(),
                    Unit.builder().name("School of Architect and Regional Planning").build(),
                    Unit.builder().name("School of Business and Management (SBM)").build(),
                    Unit.builder().name("School of Pharmacy (SF)").build(),
                    Unit.builder().name("School of Life Sciences and Technology (SITH)").build()
            );
            unitRepository.saveAll(units);
            System.out.println("✅ Unit data seeded successfully!");
        }
    }
}

