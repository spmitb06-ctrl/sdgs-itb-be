package com.sdgs.itb.infrastructure.goal.seed;

import com.sdgs.itb.entity.goal.Goal;
import com.sdgs.itb.entity.goal.GoalScholar;
import com.sdgs.itb.entity.goal.Scholar;
import com.sdgs.itb.infrastructure.goal.repository.GoalRepository;
import com.sdgs.itb.infrastructure.goal.repository.GoalScholarRepository;
import com.sdgs.itb.infrastructure.goal.repository.ScholarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalSeeder implements CommandLineRunner {

    private final GoalRepository goalRepository;
    private ScholarRepository scholarRepository;
    private GoalScholarRepository goalScholarRepository;

    @Override
    public void run(String... args) throws Exception {
        if (goalRepository.count() == 0) {
            List<Goal> goals = goalRepository.saveAll(List.of(
                    Goal.builder().id(1L).goalNumber(1).title("GOAL 1: No Poverty").description("End poverty in all its forms everywhere.").color("#e5243b").icon("/sdgs/goal1.png").editedIcon("/sdgs/edited/goal1.png").linkUrl(null).build(),
                    Goal.builder().id(2L).goalNumber(2).title("GOAL 2 : Zero Hunger").description("End hunger, achieve food security and improved nutrition.").color("#dda63a").icon("/sdgs/goal2.png").editedIcon("/sdgs/edited/goal2.png").linkUrl(null).build(),
                    Goal.builder().id(3L).goalNumber(3).title("GOAL 3: Good Health and Well-being").description("Ensure healthy lives and promote well-being for all.").color("#4c9f38").icon("/sdgs/goal3.png").editedIcon("/sdgs/edited/goal3.png").linkUrl(null).build(),
                    Goal.builder().id(4L).goalNumber(4).title("GOAL 4: Quality Education").description("Ensure inclusive and equitable quality education.").color("#c5192d").icon("/sdgs/goal4.png").editedIcon("/sdgs/edited/goal4.png").linkUrl(null).build(),
                    Goal.builder().id(5L).goalNumber(5).title("GOAL 5: Gender Equality").description("Achieve gender equality and empower all women and girls.").color("#ff3a21").icon("/sdgs/goal5.png").editedIcon("/sdgs/edited/goal5.png").linkUrl(null).build(),
                    Goal.builder().id(6L).goalNumber(6).title("GOAL 6: Clean Water and Sanitation").description("Ensure availability and sustainable management of water.").color("#26bde2").icon("/sdgs/goal6.png").editedIcon("/sdgs/edited/goal6.png").linkUrl(null).build(),
                    Goal.builder().id(7L).goalNumber(7).title("GOAL 7: Affordable and Clean Energy").description("Ensure access to affordable, reliable, sustainable energy.").color("#fcc30b").icon("/sdgs/goal7.png").editedIcon("/sdgs/edited/goal7.png").linkUrl(null).build(),
                    Goal.builder().id(8L).goalNumber(8).title("GOAL 8: Decent Work and Economic Growth").description("Promote sustained, inclusive, sustainable economic growth.").color("#a21942").icon("/sdgs/goal8.png").editedIcon("/sdgs/edited/goal8.png").linkUrl(null).build(),
                    Goal.builder().id(9L).goalNumber(9).title("GOAL 9: Industry, Innovation and Infrastructure").description("Build resilient infrastructure and foster innovation.").color("#fd6925").icon("/sdgs/goal9.png").editedIcon("/sdgs/edited/goal9.png").linkUrl(null).build(),
                    Goal.builder().id(10L).goalNumber(10).title("GOAL 10: Reduced Inequalities").description("Reduce inequality within and among countries.").color("#dd1367").icon("/sdgs/goal10.png").editedIcon("/sdgs/edited/goal10.png").linkUrl(null).build(),
                    Goal.builder().id(11L).goalNumber(11).title("GOAL 11: Sustainable Cities and Communities").description("Make cities inclusive, safe, resilient and sustainable.").color("#fd9d24").icon("/sdgs/goal11.png").editedIcon("/sdgs/edited/goal11.png").linkUrl(null).build(),
                    Goal.builder().id(12L).goalNumber(12).title("GOAL 12: Responsible Consumption and Production").description("Ensure sustainable consumption and production patterns.").color("#bf8b2e").icon("/sdgs/goal12.png").editedIcon("/sdgs/edited/goal12.png").linkUrl(null).build(),
                    Goal.builder().id(13L).goalNumber(13).title("GOAL 13: Climate Action").description("Take urgent action to combat climate change.").color("#3f7e44").icon("/sdgs/goal13.png").editedIcon("/sdgs/edited/goal13.png").linkUrl(null).build(),
                    Goal.builder().id(14L).goalNumber(14).title("GOAL 14: Life Below Water").description("Conserve and sustainably use oceans and marine resources.").color("#0a97d9").icon("/sdgs/goal14.png").editedIcon("/sdgs/edited/goal14.png").linkUrl(null).build(),
                    Goal.builder().id(15L).goalNumber(15).title("GOAL 15: Life on Land").description("Protect, restore, and promote sustainable use of ecosystems.").color("#56c02b").icon("/sdgs/goal15.png").editedIcon("/sdgs/edited/goal15.png").linkUrl(null).build(),
                    Goal.builder().id(16L).goalNumber(16).title("GOAL 16: Peace, Justice and Strong Institutions").description("Promote peaceful and inclusive societies for sustainable development.").color("#00689d").icon("/sdgs/goal16.png").editedIcon("/sdgs/edited/goal16.png").linkUrl(null).build(),
                    Goal.builder().id(17L).goalNumber(17).title("GOAL 17: Partnerships for the Goals").description("Strengthen global partnerships for sustainable development.").color("#19486a").icon("/sdgs/goal17.png").editedIcon("/sdgs/edited/goal17.png").linkUrl(null).build(),
                    Goal.builder().id(18L).goalNumber(0).title("SDG Wheel Logo").description("Logo").color("#FFFFFF").icon("/sdgs/wheel.png").linkUrl(null).build()
            ));

            List<Scholar> scholars = scholarRepository.findAll();
            for (Goal goal : goals) {
                for (Scholar scholar : scholars) {
                    GoalScholar gs = GoalScholar.builder()
                            .goal(goal)
                            .scholar(scholar)
                            .link(null)
                            .build();
                    goalScholarRepository.save(gs);
                }
            }
        }
    }
}
