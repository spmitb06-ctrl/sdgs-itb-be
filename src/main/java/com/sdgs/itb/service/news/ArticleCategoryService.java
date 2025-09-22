package com.sdgs.itb.service.news;

import com.sdgs.itb.infrastructure.faculty.dto.FacultyDTO;
import com.sdgs.itb.infrastructure.news.dto.ArticleCategoryDTO;

import java.util.List;

public interface ArticleCategoryService {
    List<ArticleCategoryDTO> getAllCategories();
    ArticleCategoryDTO getCategory(Long id);
}

