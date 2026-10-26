package com.sdgs.itb.infrastructure.ranking.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RankingSystemDTO {
    private Long id;
    private String name;
    private String code;
    private String description;
}