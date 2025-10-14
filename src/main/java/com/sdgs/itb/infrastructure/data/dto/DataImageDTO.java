package com.sdgs.itb.infrastructure.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DataImageDTO {
    private Long id;
    private String imageUrl;
}

