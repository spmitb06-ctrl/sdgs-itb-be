package com.sdgs.itb.infrastructure.report.repository;

import com.sdgs.itb.entity.report.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long>, JpaSpecificationExecutor<Report> {

    @Query("SELECT a FROM Report a WHERE LOWER(a.sourceUrl) = LOWER(:url)")
    Optional<Report> findBySourceUrl(@Param("url") String url);

    @Query("SELECT a FROM Report a WHERE LOWER(a.title) = LOWER(:title)")
    Optional<Report> findByTitleIgnoreCase(@Param("title") String title);
}
