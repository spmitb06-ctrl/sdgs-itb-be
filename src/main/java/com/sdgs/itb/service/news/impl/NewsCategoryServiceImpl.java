package com.sdgs.itb.service.news.impl;

import com.sdgs.itb.entity.news.NewsCategory;
import com.sdgs.itb.infrastructure.news.dto.NewsCategoryDTO;
import com.sdgs.itb.infrastructure.news.mapper.NewsCategoryMapper;
import com.sdgs.itb.infrastructure.news.repository.NewsCategoryRepository;
import com.sdgs.itb.service.news.NewsCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsCategoryServiceImpl implements NewsCategoryService {

    private final NewsCategoryRepository newsCategoryRepository;

    @Override
    public List<NewsCategoryDTO> getAllCategories() {
        return newsCategoryRepository.findAll()
                .stream()
                .map(NewsCategoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public NewsCategoryDTO getCategory(Long id) {
        NewsCategory newsCategory = newsCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News category not found"));
        return NewsCategoryMapper.toDTO((newsCategory));
    }
}
