package com.sdgs.itb.service.goal.impl;

import com.sdgs.itb.entity.goal.Goal;
import com.sdgs.itb.entity.goal.GoalScholar;
import com.sdgs.itb.entity.goal.Scholar;
import com.sdgs.itb.infrastructure.goal.dto.GoalDTO;
import com.sdgs.itb.infrastructure.goal.mapper.GoalMapper;
import com.sdgs.itb.infrastructure.goal.repository.GoalRepository;
import com.sdgs.itb.infrastructure.goal.repository.GoalScholarRepository;
import com.sdgs.itb.infrastructure.goal.repository.ScholarRepository;
import com.sdgs.itb.service.goal.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final ScholarRepository scholarRepository;
    private final GoalScholarRepository goalScholarRepository;

    @Override
    public List<GoalDTO> getAllGoals() {
        return goalRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(GoalMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public GoalDTO getGoal(Long id) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
        return GoalMapper.toDTO(goal);
    }

    @Override
    public GoalDTO createGoal(GoalDTO goalDTO) {
        Goal goal = GoalMapper.toEntity(goalDTO);
        Goal saved = goalRepository.save(goal);

        List<Scholar> scholars = scholarRepository.findAll();
        for (Scholar scholar : scholars) {
            GoalScholar gs = GoalScholar.builder()
                    .goal(saved)
                    .scholar(scholar)
                    .link(null) // start with no link
                    .build();
            goalScholarRepository.save(gs);
        }

        // Reload goal with scholars
        Goal reloaded = goalRepository.findById(saved.getId())
                .orElseThrow(() -> new RuntimeException("Goal not found after save"));

        return GoalMapper.toDTO(reloaded);
    }

    @Override
    public GoalDTO updateGoal(Long id, GoalDTO goalDTO) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        goal.setGoalNumber(goalDTO.getGoalNumber());
        goal.setTitle(goalDTO.getTitle());
        goal.setDescription(goalDTO.getDescription());
        goal.setColor(goalDTO.getColor());
        goal.setIcon(goalDTO.getIcon());
        goal.setEditedIcon(goalDTO.getEditedIcon());
        goal.setLinkUrl(goalDTO.getLinkUrl());

        Goal updated = goalRepository.save(goal);
        return GoalMapper.toDTO(updated);
    }

    @Override
    public void deleteGoal(Long id) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
        goalRepository.delete(goal);
    }
}


