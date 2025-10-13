package com.sdgs.itb.infrastructure.news.repository;

import com.sdgs.itb.entity.news.NewsUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NewsUnitRepository extends JpaRepository<NewsUnit, Long> {

    @Query("""
        SELECT nu FROM NewsUnit nu
        WHERE nu.news.id = :newsId
          AND nu.deletedAt IS NULL
    """)
    List<NewsUnit> findAllByNewsId(@Param("newsId") Long newsId);

    @Query("""
        SELECT nu
        FROM NewsUnit nu
        WHERE nu.news.id = :newsId
          AND nu.unit.id = :unitId
          AND nu.deletedAt IS NULL
    """)
    Optional<NewsUnit> findByNewsIdAndUnitId(
            @Param("newsId") Long newsId,
            @Param("unitId") Long unitId
    );
}
