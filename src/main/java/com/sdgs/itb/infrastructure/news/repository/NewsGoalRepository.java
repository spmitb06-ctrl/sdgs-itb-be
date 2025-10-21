package com.sdgs.itb.infrastructure.news.repository;

import com.sdgs.itb.entity.news.NewsGoal;
import com.sdgs.itb.infrastructure.news.dto.NewsGoalStatsDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NewsGoalRepository extends JpaRepository<NewsGoal, Long> {

    @Query("""
        SELECT ng FROM NewsGoal ng
        WHERE ng.news.id = :newsId
          AND ng.deletedAt IS NULL
    """)
    List<NewsGoal> findAllByNewsId(@Param("newsId") Long newsId);

    @Query("""
        SELECT ng
        FROM NewsGoal ng
        WHERE ng.news.id = :newsId
          AND ng.goal.id = :goalId
          AND ng.deletedAt IS NULL
    """)
    Optional<NewsGoal> findByNewsIdAndGoalId(
            @Param("newsId") Long newsId,
            @Param("goalId") Long goalId
    );

    @Query("""
        SELECT new com.sdgs.itb.infrastructure.news.dto.NewsGoalStatsDTO(
            g.id,
            g.goalNumber,
            g.title,
            g.color,
            g.icon,
            COUNT(ng.id)
        )
        FROM NewsGoal ng
        JOIN ng.goal g
        JOIN ng.news n
        WHERE n.deletedAt IS NULL
            AND (:year IS NULL OR EXTRACT(YEAR FROM n.eventDate) = :year)
            AND (:categoryId IS NULL OR n.newsCategory.id = :categoryId)
        GROUP BY g.id, g.goalNumber, g.title, g.color, g.icon
        ORDER BY g.goalNumber
    """)
    List<NewsGoalStatsDTO> findNewsCountByGoal(
            @Param("year") Integer year,
            @Param("categoryId") Long categoryId
    );


}
