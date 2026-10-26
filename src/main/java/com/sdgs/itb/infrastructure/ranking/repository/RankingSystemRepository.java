package com.sdgs.itb.infrastructure.ranking.repository;

import com.sdgs.itb.entity.ranking.RankingSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RankingSystemRepository extends JpaRepository<RankingSystem, Long> {

    @Query("SELECT r FROM RankingSystem r WHERE r.deletedAt IS NULL ORDER BY r.id ASC")
    List<RankingSystem> findAllActive();

    @Query("SELECT r FROM RankingSystem r WHERE LOWER(r.code) = LOWER(:code) AND r.deletedAt IS NULL")
    Optional<RankingSystem> findByCodeIgnoreCase(@Param("code") String code);
}