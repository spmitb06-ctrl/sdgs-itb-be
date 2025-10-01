package com.sdgs.itb.service.report.impl;

import com.sdgs.itb.common.exceptions.DataNotFoundException;
import com.sdgs.itb.entity.report.Report;
import com.sdgs.itb.entity.report.ReportCategory;
import com.sdgs.itb.infrastructure.report.dto.ReportDTO;
import com.sdgs.itb.infrastructure.report.mapper.ReportMapper;
import com.sdgs.itb.infrastructure.report.repository.ReportCategoryRepository;
import com.sdgs.itb.infrastructure.report.repository.ReportRepository;
import com.sdgs.itb.service.report.ReportService;
import com.sdgs.itb.service.report.specification.ReportSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final ReportCategoryRepository categoryRepository;

    @Override
    public ReportDTO createReport(ReportDTO dto) {
        Report report = ReportMapper.toEntity(dto);

        if (dto.getCategoryId() != null) {
            ReportCategory category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new DataNotFoundException("Category not found"));
            report.setReportCategory(category);

            String categoryName = category.getCategory();
            String imageUrl;

            if ("SDG".equalsIgnoreCase(categoryName)) {
                imageUrl = "/report/sdgs.png";
            } else if ("ESG".equalsIgnoreCase(categoryName)) {
                imageUrl = "/report/esg.png";
            } else {
                imageUrl = "/report/other.png";
            }

            report.setImageUrl(imageUrl);
        } else {
            // If no category, fallback image
            report.setImageUrl("/report/other.png");
        }

        return ReportMapper.toDTO(reportRepository.save(report));
    }

    @Override
    public ReportDTO updateReport(Long id, ReportDTO dto) {
        Report existing = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());
        existing.setFileUrl(dto.getFileUrl());
        existing.setYear(dto.getYear());

        ReportCategory category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Category not found"));
        existing.setReportCategory(category);

        String categoryName = category.getCategory();
        String imageUrl;

        if ("SDG".equalsIgnoreCase(categoryName)) {
            imageUrl = "/report/sdgs.png";
        } else if ("ESG".equalsIgnoreCase(categoryName)) {
            imageUrl = "/report/esg.png";
        } else {
            imageUrl = "/report/other.png";
        }

        existing.setImageUrl(imageUrl);

        return ReportMapper.toDTO(reportRepository.save(existing));
    }

    @Override
    public void deleteReport(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        report.setDeletedAt(java.time.OffsetDateTime.now());

        reportRepository.save(report);
    }

    @Override
    public ReportDTO getReport(Long id) {
        return reportRepository.findById(id)
                .filter(report -> report.getDeletedAt() == null)
                .map(ReportMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Report not found"));
    }

    @Override
    public Page<ReportDTO> getPolicies(
            String title,
            String year,
            List<Long> categoryIds,
            String sortBy,
            String sortDir,
            int page,
            int size
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Report> spec = Specification
                .where(ReportSpecification.notDeleted())
                .and(ReportSpecification.hasTitle(title))
                .and(ReportSpecification.hasYear(year))
                .and(ReportSpecification.hasCategories(categoryIds));

        return reportRepository.findAll(spec, pageable).map(ReportMapper::toDTO);
    }
}
