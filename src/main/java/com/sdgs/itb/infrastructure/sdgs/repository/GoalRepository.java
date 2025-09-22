package com.sdgs.itb.infrastructure.sdgs.repository;

import com.sdgs.itb.entity.sdgs.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    @Query("SELECT g FROM Goal g WHERE LOWER(g.title) = LOWER(:title)")
    Optional<Goal> findByTitle(@Param("title") String title);

    @Query("SELECT g FROM Goal g WHERE LOWER(g.title) = LOWER(:title)")
    List<Goal> findAllByTitle(@Param("title") String title);
}

