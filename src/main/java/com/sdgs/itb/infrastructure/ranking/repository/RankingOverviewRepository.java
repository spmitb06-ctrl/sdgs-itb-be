package com.sdgs.itb.infrastructure.ranking.repository;

import com.sdgs.itb.entity.ranking.RankingOverview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RankingOverviewRepository extends JpaRepository<RankingOverview, Long>, JpaSpecificationExecutor<RankingOverview> {

    @Query("""
        SELECT ro FROM RankingOverview ro
        WHERE ro.rankingSystem.id = :systemId
          AND ro.goal.id = :goalId
          AND ro.year = :year
          AND ro.deletedAt IS NULL
    """)
    Optional<RankingOverview> findBySystemGoalAndYear(
            @Param("systemId") Long systemId,
            @Param("goalId") Long goalId,
            @Param("year") Integer year
    );

    @Query("""
        SELECT ro FROM RankingOverview ro
        WHERE ro.rankingSystem.id = :systemId
          AND ro.year = :year
          AND ro.isPublished = true
          AND ro.deletedAt IS NULL
        ORDER BY ro.goal.goalNumber ASC
    """)
    List<RankingOverview> findPublicOverviews(
            @Param("systemId") Long systemId,
            @Param("year") Integer year
    );
}