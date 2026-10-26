package com.sdgs.itb.infrastructure.ranking.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RankingSubmissionDTO {
    private Long id;
    private Long questionId;
    private Integer submissionYear;
    private String answerText;
    private String sourceDocument;
}