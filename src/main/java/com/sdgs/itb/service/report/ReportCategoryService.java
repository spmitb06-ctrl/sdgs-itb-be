package com.sdgs.itb.service.report;

import com.sdgs.itb.infrastructure.report.dto.ReportCategoryDTO;

import java.util.List;

public interface ReportCategoryService {
//    ReportCategoryDTO createReportCategory(ReportCategoryDTO dto);
//    ReportCategoryDTO updateReportCategory(Long id, ReportCategoryDTO dto);
//    void deleteReportCategory(Long id);
    List<ReportCategoryDTO> getAllCategories();
    ReportCategoryDTO getCategory(Long id);
}

