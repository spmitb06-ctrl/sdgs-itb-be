package com.sdgs.itb.infrastructure.goal.seed;

import com.sdgs.itb.entity.goal.Scholar;
import com.sdgs.itb.infrastructure.goal.repository.ScholarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ScholarSeeder implements CommandLineRunner {

    private final ScholarRepository scholarRepository;

    @Override
    public void run(String... args) {
        if (scholarRepository.count() == 0) {
            scholarRepository.saveAll(List.of(
                    Scholar.builder().name("paper").iconUrl("/news/icons/paper.svg").build(),
                    Scholar.builder().name("project").iconUrl("/news/icons/project.svg").build(),
                    Scholar.builder().name("patent").iconUrl("/news/icons/patent.svg").build(),
                    Scholar.builder().name("outreach").iconUrl("/news/icons/community-service.svg").build(),
                    Scholar.builder().name("thesis").iconUrl("/news/icons/thesis.svg").build()
            ));
        }
    }
}
