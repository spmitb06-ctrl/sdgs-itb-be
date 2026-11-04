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
          AND (:year IS NULL OR q.year = :year)
          AND q.deletedAt IS NULL
        ORDER BY q.code ASC
    """)
    List<RankingQuestion> findRootQuestionsBySystemAndGoalAndYear(
            @Param("systemId") Long systemId,
            @Param("goalId") Long goalId,
            @Param("year") Integer year
    );

    @Query("""
        SELECT q FROM RankingQuestion q
        WHERE q.rankingSystem.id = :systemId
          AND q.level = 1
          AND (:year IS NULL OR q.year = :year)
          AND q.deletedAt IS NULL
        ORDER BY q.code ASC
    """)
    List<RankingQuestion> findAllRootQuestionsBySystemAndYear(
            @Param("systemId") Long systemId,
            @Param("year") Integer year
    );

    // Cek duplikasi kode spesifik pada Sistem, Goal, dan Tahun yang sama
    boolean existsByRankingSystemIdAndGoalIdAndYearAndCodeAndDeletedAtIsNull(
            Long rankingSystemId,
            Long goalId,
            Integer year,
            String code
    );

    boolean existsByRankingSystemIdAndGoalIdAndYearAndCodeAndIdNotAndDeletedAtIsNull(
            Long rankingSystemId,
            Long goalId,
            Integer year,
            String code,
            Long id
    );
}