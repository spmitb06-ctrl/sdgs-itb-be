package com.sdgs.itb.infrastructure.data.mapper;

import com.sdgs.itb.entity.data.DataCategory;
import com.sdgs.itb.infrastructure.data.dto.DataCategoryDTO;

public class DataCategoryMapper {

    public static DataCategory toEntity(DataCategoryDTO dto) {
        DataCategory dataCategory = new DataCategory();
        dataCategory.setCategory(dto.getCategory());

        return dataCategory;
    }

    public static DataCategoryDTO toDTO(DataCategory dataCategory) {
        DataCategoryDTO dto = new DataCategoryDTO();
        dto.setId(dataCategory.getId());
        dto.setCategory(dataCategory.getCategory());

        return dto;
    }
}


