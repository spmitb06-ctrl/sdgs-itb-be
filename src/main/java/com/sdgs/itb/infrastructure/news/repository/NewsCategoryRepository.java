package com.sdgs.itb.infrastructure.news.repository;

import com.sdgs.itb.entity.news.NewsCategory;
import com.sdgs.itb.infrastructure.news.dto.NewsCategoryStatsDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NewsCategoryRepository extends JpaRepository<NewsCategory, Long> {

    @Query("""
        SELECT new com.sdgs.itb.infrastructure.news.dto.NewsCategoryStatsDTO(
            c.id,
            c.category,
            COUNT(DISTINCT n.id)
        )
        FROM News n
        JOIN n.newsCategory c
        JOIN n.newsGoals ng
        JOIN ng.goal g
        WHERE n.deletedAt IS NULL
          AND (:year IS NULL OR EXTRACT(YEAR FROM n.eventDate) = :year)
          AND (:goalId IS NULL OR g.id = :goalId)
        GROUP BY c.id, c.category
        ORDER BY c.category
    """)
    List<NewsCategoryStatsDTO> findNewsCountByCategory(
            @Param("year") Integer year,
            @Param("goalId") Long goalId
    );
}


