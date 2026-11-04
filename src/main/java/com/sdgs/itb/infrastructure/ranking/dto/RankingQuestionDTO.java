package com.sdgs.itb.infrastructure.ranking.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RankingQuestionDTO {
    private Long id;
    private Long rankingSystemId;
    private String rankingSystemName;
    private Long goalId;
    private Integer goalNumber;
    private Long parentId;
    private Integer year; // Field tahun metrik
    private String code;
    private String title;
    private String content;
    private Integer level;
    private List<RankingQuestionDTO> children;
    private RankingSubmissionDTO submission;
}