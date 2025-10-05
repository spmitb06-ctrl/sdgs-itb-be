package com.sdgs.itb.infrastructure.unit.seed;

import com.sdgs.itb.entity.unit.Unit;
import com.sdgs.itb.infrastructure.unit.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UnitTypeDataSeeder implements CommandLineRunner {

    private final UnitRepository unitRepository;

    @Override
    public void run(String... args) {
        if (unitRepository.count() == 0) {
            List<Unit> units = List.of(
                    Unit.builder().name("Research Group").build(),
                    Unit.builder().name("Research Center").build(),
                    Unit.builder().name("School or Faculty").build()
            );
            unitRepository.saveAll(units);
            System.out.println("✅ Unit type data seeded successfully!");
        }
    }
}

