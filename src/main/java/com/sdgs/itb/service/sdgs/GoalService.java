package com.sdgs.itb.service.sdgs;

import com.sdgs.itb.infrastructure.sdgs.dto.GoalDTO;

import java.util.List;

public interface GoalService {
    List<GoalDTO> getAllGoals();
    GoalDTO getGoal(Long id);
    GoalDTO createGoal(GoalDTO goalDTO);
    GoalDTO updateGoal(Long id, GoalDTO goalDTO);
    void deleteGoal(Long id);
}

