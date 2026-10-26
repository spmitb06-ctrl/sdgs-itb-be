package com.sdgs.itb.infrastructure.ranking.repository;

import com.sdgs.itb.entity.ranking.RankingSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RankingSubmissionRepository extends JpaRepository<RankingSubmission, Long> {

    @Query("""
        SELECT s FROM RankingSubmission s
        WHERE s.question.id = :questionId
          AND s.submissionYear = :year
          AND s.deletedAt IS NULL
    """)
    Optional<RankingSubmission> findByQuestionIdAndYear(
            @Param("questionId") Long questionId,
            @Param("year") Integer year
    );

    @Query("""
        SELECT s FROM RankingSubmission s
        WHERE s.question.id IN :questionIds
          AND s.submissionYear = :year
          AND s.deletedAt IS NULL
    """)
    List<RankingSubmission> findByQuestionIdsAndYear(
            @Param("questionIds") List<Long> questionIds,
            @Param("year") Integer year
    );
}