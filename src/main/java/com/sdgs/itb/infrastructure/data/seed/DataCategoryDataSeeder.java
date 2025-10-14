package com.sdgs.itb.infrastructure.data.seed;

import com.sdgs.itb.entity.data.DataCategory;
import com.sdgs.itb.infrastructure.data.repository.DataCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataCategoryDataSeeder implements CommandLineRunner {

    private final DataCategoryRepository dataCategoryRepository;

    @Override
    public void run(String... args) {
        if (dataCategoryRepository.count() == 0) {
            List<DataCategory> categories = List.of(
                    DataCategory.builder().category("Other").build()
            );
            dataCategoryRepository.saveAll(categories);
            System.out.println("✅ Data Category data seeded successfully!");
        }
    }
}

