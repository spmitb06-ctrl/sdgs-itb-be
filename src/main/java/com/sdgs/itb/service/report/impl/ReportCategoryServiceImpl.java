package com.sdgs.itb.service.report.impl;

import com.sdgs.itb.entity.report.ReportCategory;
import com.sdgs.itb.infrastructure.report.dto.ReportCategoryDTO;
import com.sdgs.itb.infrastructure.report.mapper.ReportCategoryMapper;
import com.sdgs.itb.infrastructure.report.repository.ReportCategoryRepository;
import com.sdgs.itb.service.report.ReportCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportCategoryServiceImpl implements ReportCategoryService {

    private final ReportCategoryRepository reportCategoryRepository;

    @Override
    public List<ReportCategoryDTO> getAllCategories() {
        return reportCategoryRepository.findAll()
                .stream()
                .map(ReportCategoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReportCategoryDTO getCategory(Long id) {
        ReportCategory reportCategory = reportCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report category not found"));
        return ReportCategoryMapper.toDTO((reportCategory));
    }
}
