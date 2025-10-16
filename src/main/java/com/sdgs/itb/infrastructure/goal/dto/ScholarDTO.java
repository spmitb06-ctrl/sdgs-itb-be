package com.sdgs.itb.infrastructure.goal.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScholarDTO {
    private Long id;
    private String name;
    private String iconUrl;

}

