package com.sdgs.itb.infrastructure.ranking.seed;

import com.sdgs.itb.entity.ranking.RankingSystem;
import com.sdgs.itb.infrastructure.ranking.repository.RankingSystemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RankingSystemSeeder implements CommandLineRunner {

    private final RankingSystemRepository rankingSystemRepository;

    @Override
    public void run(String... args) {
        if (rankingSystemRepository.count() == 0) {
            List<RankingSystem> systems = List.of(
                    RankingSystem.builder()
                            .name("Times Higher Education")
                            .code("THE")
                            .description("THE Impact Rankings assesses global universities against the United Nations’ Sustainable Development Goals (SDGs).")
                            .build(),
                    RankingSystem.builder()
                            .name("QS World University Rankings")
                            .code("QS")
                            .description("QS Sustainability and World University Rankings framework.")
                            .build(),
                    RankingSystem.builder()
                            .name("UI GreenMetric")
                            .code("UIGM")
                            .description("UI GreenMetric World University Rankings on campus sustainability.")
                            .build()
            );
            rankingSystemRepository.saveAll(systems);
            System.out.println("✅ Ranking Systems data seeded successfully!");
        }
    }
}