package com.sdgs.itb.service.report;

import com.sdgs.itb.infrastructure.report.dto.ReportDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ReportService {
    ReportDTO createReport(ReportDTO dto);
    ReportDTO updateReport(Long id, ReportDTO dto);
    void deleteReport(Long id);
    ReportDTO getReport(Long id);
    Page<ReportDTO> getPolicies(
            String title,
            String year,
            List<Long> categoryIds,
            String sortBy,
            String sortDir,
            int page,
            int size
    );
}
