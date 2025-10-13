package com.sdgs.itb.infrastructure.policy.repository;

import com.sdgs.itb.entity.policy.PolicyGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PolicyGoalRepository extends JpaRepository<PolicyGoal, Long> {

    @Query("""
        SELECT pg FROM PolicyGoal pg
        WHERE pg.policy.id = :policyId
          AND pg.deletedAt IS NULL
    """)
    List<PolicyGoal> findAllByPolicyId(@Param("policyId") Long policyId);

    @Query("""
        SELECT pg
        FROM PolicyGoal pg
        WHERE pg.policy.id = :policyId
          AND pg.goal.id = :goalId
          AND pg.deletedAt IS NULL
    """)
    Optional<PolicyGoal> findByPolicyIdAndGoalId(
            @Param("policyId") Long policyId,
            @Param("goalId") Long goalId
    );
}
