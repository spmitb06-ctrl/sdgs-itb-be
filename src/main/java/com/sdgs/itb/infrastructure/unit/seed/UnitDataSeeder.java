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
                    Unit.builder().name("Faculty of Mathematics and Natural Sciences (FMIPA)").abbreviation("FMIPA").build(),
                    Unit.builder().name("Faculty of Earth Sciences and Technology (FITB)").abbreviation("FITB").build(),
                    Unit.builder().name("Faculty of Mining and Petroleum Engineering (FTTM)").abbreviation("FTTM").build(),
                    Unit.builder().name("Faculty of Industrial Technology (FTI)").abbreviation("FTI").build(),
                    Unit.builder().name("Faculty of Mechanical and Aerospace Engineering (FTMD)").abbreviation("FTMD").build(),
                    Unit.builder().name("Faculty of Civil and Environmental Engineering (FTSL)").abbreviation("FTSL").build(),
                    Unit.builder().name("School of Electrical Engineering and Informatics (STEI)").abbreviation("STEI").build(),
                    Unit.builder().name("Faculty of Art and Design (FSRD)").abbreviation("FSRD").build(),
                    Unit.builder().name("School of Architect and Regional Planning (SAPPK)").abbreviation("SAPPK").build(),
                    Unit.builder().name("School of Business and Management (SBM)").abbreviation("SBM").build(),
                    Unit.builder().name("School of Pharmacy (SF)").abbreviation("SF").build(),
                    Unit.builder().name("School of Life Sciences and Technology (SITH)").abbreviation("SITH").build(),
                    Unit.builder().name("World Class University (WCU)").abbreviation("WCU").build(),
                    Unit.builder().name("Direktorat Pengabdian Masyarakat dan Layanan Kepakaran (DPMK)").abbreviation("DPMK").build(),
                    Unit.builder().name("Other").abbreviation("Other").build()
            );
            unitRepository.saveAll(units);
            System.out.println("✅ Unit data seeded successfully!");
        }
    }
}

