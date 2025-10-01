package com.sdgs.itb.infrastructure.news.seed;

import com.sdgs.itb.entity.news.NewsCategory;
import com.sdgs.itb.infrastructure.news.repository.NewsCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NewsCategoryDataSeeder implements CommandLineRunner {

    private final NewsCategoryRepository newsCategoryRepository;

    @Override
    public void run(String... args) {
        if (newsCategoryRepository.count() == 0) {
            List<NewsCategory> categories = List.of(
                    NewsCategory.builder().category("Community Service").build(),
                    NewsCategory.builder().category("Publication").build(),
                    NewsCategory.builder().category("Research").build()
            );
            newsCategoryRepository.saveAll(categories);
            System.out.println("✅ News Category data seeded successfully!");
        }
    }
}

