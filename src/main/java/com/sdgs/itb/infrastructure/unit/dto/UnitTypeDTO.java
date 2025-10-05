package com.sdgs.itb.infrastructure.unit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UnitTypeDTO {
    private Long id;
    private String name;
}

