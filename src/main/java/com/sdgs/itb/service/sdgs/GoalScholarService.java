package com.sdgs.itb.service.sdgs;

import com.sdgs.itb.infrastructure.sdgs.dto.GoalScholarDTO;
import com.sdgs.itb.infrastructure.sdgs.dto.GoalScholarRequest;

import java.util.List;

public interface GoalScholarService {
    List<GoalScholarDTO> getByGoal(Long goalId);
    GoalScholarDTO getOne(Long goalId, Long scholarId);
    GoalScholarDTO updateLink(GoalScholarRequest request);
    void updateAllCounts();
}
