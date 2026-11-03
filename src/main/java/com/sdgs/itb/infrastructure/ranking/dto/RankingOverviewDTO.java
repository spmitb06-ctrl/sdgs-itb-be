package com.sdgs.itb.infrastructure.ranking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sdgs.itb.infrastructure.goal.dto.GoalDTO;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RankingOverviewDTO {
    private Long id;
    private Long rankingSystemId;
    private String rankingSystemName;
    private String rankingSystemCode;
    private Long goalId;
    private GoalDTO goal;
    private Integer year;
    private String rankPosition;
    private Double score;
    private String content;
    private Boolean isPublished;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataEndDate;
}