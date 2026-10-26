package com.sdgs.itb.infrastructure.ranking.mapper;

import com.sdgs.itb.entity.ranking.RankingOverview;
import com.sdgs.itb.entity.ranking.RankingQuestion;
import com.sdgs.itb.entity.ranking.RankingSubmission;
import com.sdgs.itb.entity.ranking.RankingSystem;
import com.sdgs.itb.infrastructure.goal.mapper.GoalMapper;
import com.sdgs.itb.infrastructure.ranking.dto.RankingOverviewDTO;
import com.sdgs.itb.infrastructure.ranking.dto.RankingQuestionDTO;
import com.sdgs.itb.infrastructure.ranking.dto.RankingSubmissionDTO;
import com.sdgs.itb.infrastructure.ranking.dto.RankingSystemDTO;

import java.util.stream.Collectors;

public class RankingMapper {

    public static RankingSystemDTO toDTO(RankingSystem entity) {
        if (entity == null) return null;
        return RankingSystemDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .description(entity.getDescription())
                .build();
    }

    public static RankingSystem toEntity(RankingSystemDTO dto) {
        if (dto == null) return null;
        return RankingSystem.builder()
                .id(dto.getId())
                .name(dto.getName())
                .code(dto.getCode())
                .description(dto.getDescription())
                .build();
    }

    public static RankingSubmissionDTO toDTO(RankingSubmission entity) {
        if (entity == null) return null;
        return RankingSubmissionDTO.builder()
                .id(entity.getId())
                .questionId(entity.getQuestion() != null ? entity.getQuestion().getId() : null)
                .submissionYear(entity.getSubmissionYear())
                .answerText(entity.getAnswerText())
                .sourceDocument(entity.getSourceDocument())
                .build();
    }

    public static RankingQuestionDTO toDTO(RankingQuestion entity) {
        if (entity == null) return null;
        return RankingQuestionDTO.builder()
                .id(entity.getId())
                .rankingSystemId(entity.getRankingSystem() != null ? entity.getRankingSystem().getId() : null)
                .rankingSystemName(entity.getRankingSystem() != null ? entity.getRankingSystem().getName() : null)
                .goalId(entity.getGoal() != null ? entity.getGoal().getId() : null)
                .goalNumber(entity.getGoal() != null ? entity.getGoal().getGoalNumber() : null)
                .parentId(entity.getParent() != null ? entity.getParent().getId() : null)
                .code(entity.getCode())
                .title(entity.getTitle())
                .content(entity.getContent())
                .level(entity.getLevel())
                .children(entity.getChildren() != null ? entity.getChildren().stream()
                        .filter(c -> c.getDeletedAt() == null)
                        .map(RankingMapper::toDTO)
                        .collect(Collectors.toList()) : null)
                .build();
    }

    public static RankingOverviewDTO toDTO(RankingOverview entity) {
        if (entity == null) return null;
        return RankingOverviewDTO.builder()
                .id(entity.getId())
                .rankingSystemId(entity.getRankingSystem() != null ? entity.getRankingSystem().getId() : null)
                .rankingSystemName(entity.getRankingSystem() != null ? entity.getRankingSystem().getName() : null)
                .rankingSystemCode(entity.getRankingSystem() != null ? entity.getRankingSystem().getCode() : null)
                .goalId(entity.getGoal() != null ? entity.getGoal().getId() : null)
                .goal(entity.getGoal() != null ? GoalMapper.toDTO(entity.getGoal()) : null)
                .year(entity.getYear())
                .rankPosition(entity.getRankPosition())
                .score(entity.getScore())
                .content(entity.getContent())
                .isPublished(entity.getIsPublished())
                .build();
    }
}