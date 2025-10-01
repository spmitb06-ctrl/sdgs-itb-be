package com.sdgs.itb.infrastructure.report.repository;

import com.sdgs.itb.entity.report.ReportCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportCategoryRepository extends JpaRepository<ReportCategory, Long> {
}


