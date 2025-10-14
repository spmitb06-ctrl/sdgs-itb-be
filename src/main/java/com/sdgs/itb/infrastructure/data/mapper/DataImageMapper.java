package com.sdgs.itb.infrastructure.data.mapper;

import com.sdgs.itb.entity.data.DataImage;
import com.sdgs.itb.infrastructure.data.dto.DataImageDTO;

public class DataImageMapper {

    public static DataImageDTO toDTO(DataImage dataImage) {
        return DataImageDTO.builder()
                .id(dataImage.getId())
                .imageUrl(dataImage.getImageUrl())
                .build();
    }
}


