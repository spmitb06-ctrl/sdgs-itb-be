package com.sdgs.itb.infrastructure.goal.repository;

import com.sdgs.itb.entity.goal.GoalScholar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalScholarRepository extends JpaRepository<GoalScholar, Long> {
    List<GoalScholar> findByGoalId(Long goalId);
}
