package com.sdgs.itb.service.news.impl;

import com.sdgs.itb.entity.news.ArticleCategory;
import com.sdgs.itb.infrastructure.news.dto.ArticleCategoryDTO;
import com.sdgs.itb.infrastructure.news.mapper.ArticleCategoryMapper;
import com.sdgs.itb.infrastructure.news.repository.ArticleCategoryRepository;
import com.sdgs.itb.service.news.ArticleCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleCategoryServiceImpl implements ArticleCategoryService {

    private final ArticleCategoryRepository articleCategoryRepository;

    @Override
    public List<ArticleCategoryDTO> getAllCategories() {
        return articleCategoryRepository.findAll()
                .stream()
                .map(ArticleCategoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ArticleCategoryDTO getCategory(Long id) {
        ArticleCategory articleCategory = articleCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article category not found"));
        return ArticleCategoryMapper.toDTO((articleCategory));
    }
}
