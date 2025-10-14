package com.sdgs.itb.infrastructure.data.dto;

import com.sdgs.itb.infrastructure.unit.dto.UnitDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataUnitDTO {
    private Long id;
    private OffsetDateTime createdAt;
    private UnitDTO units;
}
