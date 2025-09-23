package com.sdgs.itb.infrastructure.policy.seed;

import com.sdgs.itb.entity.news.ArticleCategory;
import com.sdgs.itb.entity.policy.PolicyCategory;
import com.sdgs.itb.infrastructure.news.repository.ArticleCategoryRepository;
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
                    PolicyCategory.builder().category("Category A").build(),
                    PolicyCategory.builder().category("Category B").build(),
                    PolicyCategory.builder().category("Category C").build(),
                    PolicyCategory.builder().category("Category D").build()
            );
            policyCategoryRepository.saveAll(categories);
            System.out.println("✅ Policy Category data seeded successfully!");
        }
    }
}

