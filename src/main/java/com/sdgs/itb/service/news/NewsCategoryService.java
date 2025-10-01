package com.sdgs.itb.service.news;

import com.sdgs.itb.infrastructure.news.dto.NewsCategoryDTO;

import java.util.List;

public interface NewsCategoryService {
    List<NewsCategoryDTO> getAllCategories();
    NewsCategoryDTO getCategory(Long id);
}

