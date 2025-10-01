package com.sdgs.itb.service.goal.impl;

import com.sdgs.itb.entity.goal.Goal;
import com.sdgs.itb.entity.goal.GoalScholar;
import com.sdgs.itb.entity.goal.Scholar;
import com.sdgs.itb.infrastructure.goal.dto.GoalScholarDTO;
import com.sdgs.itb.infrastructure.goal.dto.GoalScholarRequest;
import com.sdgs.itb.infrastructure.goal.repository.GoalRepository;
import com.sdgs.itb.infrastructure.goal.repository.GoalScholarRepository;
import com.sdgs.itb.infrastructure.goal.repository.ScholarRepository;
import com.sdgs.itb.service.goal.GoalScholarService;
import com.sdgs.itb.service.typesense.TypesenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoalScholarServiceImpl implements GoalScholarService {

    private final GoalScholarRepository goalScholarRepository;
    private final GoalRepository goalRepository;
    private final ScholarRepository scholarRepository;
    private final TypesenseService typesenseService;

    @Override
    public List<GoalScholarDTO> getByGoal(Long goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        return goal.getGoalScholars()
                .stream()
                .map(gs -> GoalScholarDTO.builder()
                        .scholarId(gs.getScholar().getId())
                        .scholarName(gs.getScholar().getName())
                        .link(gs.getLink())
                        .count(gs.getCount())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public GoalScholarDTO getOne(Long goalId, Long scholarId) {
        GoalScholar gs = goalScholarRepository.findAll()
                .stream()
                .filter(g -> g.getGoal().getId().equals(goalId) && g.getScholar().getId().equals(scholarId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("GoalScholar not found"));

        return GoalScholarDTO.builder()
                .scholarId(gs.getScholar().getId())
                .scholarName(gs.getScholar().getName())
                .link(gs.getLink())
                .count(gs.getCount())
                .build();
    }

    @Override
    public GoalScholarDTO updateLink(GoalScholarRequest request) {
        Goal goal = goalRepository.findById(request.getGoalId())
                .orElseThrow(() -> new RuntimeException("Goal not found"));
        Scholar scholar = scholarRepository.findById(request.getScholarId())
                .orElseThrow(() -> new RuntimeException("Scholar not found"));

        GoalScholar gs = goalScholarRepository.findAll()
                .stream()
                .filter(g -> g.getGoal().getId().equals(goal.getId()) && g.getScholar().getId().equals(scholar.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("GoalScholar not found"));

        gs.setLink(request.getLink());
        GoalScholar saved = goalScholarRepository.save(gs);

        return GoalScholarDTO.builder()
                .scholarId(saved.getScholar().getId())
                .scholarName(saved.getScholar().getName())
                .link(saved.getLink())
                .build();
    }

    @Override
    @Transactional
    public void updateAllCounts() {
        List<GoalScholar> allGoalScholars = goalScholarRepository.findAll();

        for (GoalScholar gs : allGoalScholars) {
            String collection = gs.getScholar().getName(); // scholar name = collection in Typesense
            String sdg = "GOAL " + gs.getGoal().getId() + ": " + gs.getGoal().getTitle(); // SDG string

            int count = typesenseService.searchCount(collection, sdg);

            gs.setCount(count);
        }

        goalScholarRepository.saveAll(allGoalScholars);
    }
}
