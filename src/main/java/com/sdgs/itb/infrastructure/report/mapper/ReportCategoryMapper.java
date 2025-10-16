package com.sdgs.itb.infrastructure.report.mapper;

import com.sdgs.itb.entity.report.ReportCategory;
import com.sdgs.itb.infrastructure.report.dto.ReportCategoryDTO;

public class ReportCategoryMapper {
    public static ReportCategory toEntity(ReportCategoryDTO dto) {
        ReportCategory reportCategory = new ReportCategory();
        reportCategory.setCategory(dto.getCategory());
        reportCategory.setColor(dto.getColor());
        reportCategory.setIconUrl(dto.getIconUrl());

        return reportCategory;
    }

    public static ReportCategoryDTO toDTO(ReportCategory reportCategory) {
        return ReportCategoryDTO.builder()
                .id(reportCategory.getId())
                .category(reportCategory.getCategory())
                .color(reportCategory.getColor())
                .iconUrl(reportCategory.getIconUrl())
                .build();
    }
}


