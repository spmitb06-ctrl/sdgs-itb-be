package com.sdgs.itb.infrastructure.report.seed;

import com.sdgs.itb.entity.report.ReportCategory;
import com.sdgs.itb.infrastructure.report.repository.ReportCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReportCategoryDataSeeder implements CommandLineRunner {

    private final ReportCategoryRepository reportCategoryRepository;

    @Override
    public void run(String... args) {
        if (reportCategoryRepository.count() == 0) {
            List<ReportCategory> categories = List.of(
                    ReportCategory.builder().category("SDG").color("#2563EB").build(),
                    ReportCategory.builder().category("ESG").color("#16A34A").build(),
                    ReportCategory.builder().category("Other").color("#6B7280").build()
            );
            reportCategoryRepository.saveAll(categories);
            System.out.println("✅ Report Category data seeded successfully!");
        }
    }
}

