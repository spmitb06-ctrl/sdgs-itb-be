package com.sdgs.itb.infrastructure.carousel.seed;

import com.sdgs.itb.entity.carousel.CarouselCategory;
import com.sdgs.itb.infrastructure.carousel.repository.CarouselCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CarouselCategoryDataSeeder implements CommandLineRunner {

    private final CarouselCategoryRepository carouselCategoryRepository;

    @Override
    public void run(String... args) {
        if (carouselCategoryRepository.count() == 0) {
            List<CarouselCategory> categories = List.of(
                    CarouselCategory.builder().category("News").color("#005AAB").build(),
                    CarouselCategory.builder().category("Program").color("#2E8B57").build()
            );
            carouselCategoryRepository.saveAll(categories);
            System.out.println("✅ Carousel Category data seeded successfully!");
        }
    }
}

