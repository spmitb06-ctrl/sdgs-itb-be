package com.sdgs.itb.infrastructure.news.seed;

import com.sdgs.itb.entity.news.ArticleCategory;
import com.sdgs.itb.infrastructure.news.repository.ArticleCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ArticleCategoryDataSeeder implements CommandLineRunner {

    private final ArticleCategoryRepository articleCategoryRepository;

    @Override
    public void run(String... args) {
        if (articleCategoryRepository.count() == 0) {
            List<ArticleCategory> categories = List.of(
                    ArticleCategory.builder().category("Community Service").build(),
                    ArticleCategory.builder().category("Publication").build(),
                    ArticleCategory.builder().category("Research").build()
            );
            articleCategoryRepository.saveAll(categories);
            System.out.println("✅ Article Category data seeded successfully!");
        }
    }
}

