package com.sdgs.itb.service.goal;

import com.sdgs.itb.infrastructure.goal.dto.GoalDTO;

import java.util.List;

public interface GoalService {
    List<GoalDTO> getAllGoals();
    GoalDTO getGoal(Long id);
    GoalDTO createGoal(GoalDTO goalDTO);
    GoalDTO updateGoal(Long id, GoalDTO goalDTO);
    void deleteGoal(Long id);
}

