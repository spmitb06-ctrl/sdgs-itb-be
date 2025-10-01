package com.sdgs.itb.service.goal;

import com.sdgs.itb.infrastructure.goal.dto.GoalScholarDTO;
import com.sdgs.itb.infrastructure.goal.dto.GoalScholarRequest;

import java.util.List;

public interface GoalScholarService {
    List<GoalScholarDTO> getByGoal(Long goalId);
    GoalScholarDTO getOne(Long goalId, Long scholarId);
    GoalScholarDTO updateLink(GoalScholarRequest request);
    void updateAllCounts();
}
