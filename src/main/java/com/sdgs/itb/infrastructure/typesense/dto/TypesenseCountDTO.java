package com.sdgs.itb.infrastructure.typesense.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TypesenseCountDTO {
    private String table;
    private String sdg;
    private int count;
}

