package com.sdgs.itb.service.data;

import com.sdgs.itb.infrastructure.data.dto.DataCategoryDTO;

import java.util.List;

public interface DataCategoryService {
    DataCategoryDTO createCategory(DataCategoryDTO dto);
    DataCategoryDTO updateCategory(Long id, DataCategoryDTO dto);
    void deleteCategory(Long id);
    List<DataCategoryDTO> getCategories();
    DataCategoryDTO getCategory(Long id);
}

