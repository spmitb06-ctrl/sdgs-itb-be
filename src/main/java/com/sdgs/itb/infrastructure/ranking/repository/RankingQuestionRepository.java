package com.sdgs.itb.infrastructure.ranking.repository;

import com.sdgs.itb.entity.ranking.RankingQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RankingQuestionRepository extends JpaRepository<RankingQuestion, Long>, JpaSpecificationExecutor<RankingQuestion> {

    @Query("""
        SELECT q FROM RankingQuestion q
        WHERE q.rankingSystem.id = :systemId
          AND q.goal.id = :goalId
          AND q.level = 1
          AND q.deletedAt IS NULL
        ORDER BY q.code ASC
    """)
    List<RankingQuestion> findRootQuestionsBySystemAndGoal(
            @Param("systemId") Long systemId,
            @Param("goalId") Long goalId
    );

    @Query("""
        SELECT q FROM RankingQuestion q
        WHERE q.rankingSystem.id = :systemId
          AND q.level = 1
          AND q.deletedAt IS NULL
        ORDER BY q.code ASC
    """)
    List<RankingQuestion> findAllRootQuestionsBySystem(@Param("systemId") Long systemId);
}