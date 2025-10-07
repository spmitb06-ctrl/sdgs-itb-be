package com.sdgs.itb.infrastructure.unit.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UnitDTO {
    private Long id;
    private Long organizationId;
    private String name;
    private String abbreviation;
    private Long typeId;
    private String typeName;
}

