package com.sdgs.itb.infrastructure.news.repository;

import com.sdgs.itb.entity.news.NewsGoal;
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
}
