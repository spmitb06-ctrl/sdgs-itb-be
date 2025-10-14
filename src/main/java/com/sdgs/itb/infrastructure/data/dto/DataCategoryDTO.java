package com.sdgs.itb.infrastructure.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DataCategoryDTO {
    private Long id;
    private String category;
}

