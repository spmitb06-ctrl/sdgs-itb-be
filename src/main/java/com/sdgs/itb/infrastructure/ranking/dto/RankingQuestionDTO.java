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
    private String code;
    private String title;
    private String content; // Konten tiap level (1, 2, atau 3)
    private Integer level;
    private List<RankingQuestionDTO> children;
    private RankingSubmissionDTO submission; // Menampung submission tahun terpilih saat get detail
}