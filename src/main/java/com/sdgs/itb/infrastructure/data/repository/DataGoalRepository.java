package com.sdgs.itb.infrastructure.data.repository;

import com.sdgs.itb.entity.data.DataGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DataGoalRepository extends JpaRepository<DataGoal, Long> {

    @Query("""
        SELECT dg FROM DataGoal dg
        WHERE dg.data.id = :dataId
          AND dg.deletedAt IS NULL
    """)
    List<DataGoal> findAllByDataId(@Param("dataId") Long dataId);

    @Query("""
        SELECT dg
        FROM DataGoal dg
        WHERE dg.data.id = :dataId
          AND dg.goal.id = :goalId
          AND dg.deletedAt IS NULL
    """)
    Optional<DataGoal> findByDataIdAndGoalId(
            @Param("dataId") Long dataId,
            @Param("goalId") Long goalId
    );
}
