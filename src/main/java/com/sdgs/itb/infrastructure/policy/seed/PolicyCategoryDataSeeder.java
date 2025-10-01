package com.sdgs.itb.infrastructure.policy.seed;

import com.sdgs.itb.entity.policy.PolicyCategory;
import com.sdgs.itb.infrastructure.policy.repository.PolicyCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PolicyCategoryDataSeeder implements CommandLineRunner {

    private final PolicyCategoryRepository policyCategoryRepository;

    @Override
    public void run(String... args) {
        if (policyCategoryRepository.count() == 0) {
            List<PolicyCategory> categories = List.of(
                    PolicyCategory.builder().category("ITB").color("#005AAB").build(),
                    PolicyCategory.builder().category("National").color("#D97706").build(),
                    PolicyCategory.builder().category("International").color("#16A34A").build()
            );
            policyCategoryRepository.saveAll(categories);
            System.out.println("✅ Policy Category data seeded successfully!");
        }
    }
}

