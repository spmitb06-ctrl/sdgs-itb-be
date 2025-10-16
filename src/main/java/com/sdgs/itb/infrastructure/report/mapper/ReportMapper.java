package com.sdgs.itb.infrastructure.report.mapper;

import com.sdgs.itb.entity.report.Report;
import com.sdgs.itb.infrastructure.report.dto.ReportDTO;

public class ReportMapper {
    public static Report toEntity(ReportDTO dto) {
        Report report = new Report();
        report.setTitle(dto.getTitle());
        report.setDescription(dto.getDescription());
        report.setFileUrl(dto.getFileUrl());
        report.setYear(dto.getYear());
        report.setImageUrl(dto.getImageUrl());
        report.setSourceUrl(dto.getSourceUrl());

        return report;
    }

    public static ReportDTO toDTO(Report report) {
        ReportDTO dto = new ReportDTO();
        dto.setId(report.getId());
        dto.setTitle(report.getTitle());
        dto.setDescription(report.getDescription());
        dto.setFileUrl(report.getFileUrl());
        dto.setYear(report.getYear());
        dto.setImageUrl(report.getImageUrl());
        dto.setSourceUrl(report.getSourceUrl());

        if (report.getReportCategory() != null) {
            dto.setCategoryId(report.getReportCategory().getId());
            dto.setCategoryName(report.getReportCategory().getCategory());
            dto.setCategoryColor(report.getReportCategory().getColor());
        }

        return dto;
    }
}

