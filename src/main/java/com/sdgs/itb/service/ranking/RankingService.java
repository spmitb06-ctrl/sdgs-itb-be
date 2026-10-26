package com.sdgs.itb.service.ranking;

import com.sdgs.itb.infrastructure.ranking.dto.RankingOverviewDTO;
import com.sdgs.itb.infrastructure.ranking.dto.RankingQuestionDTO;
import com.sdgs.itb.infrastructure.ranking.dto.RankingSubmissionDTO;
import com.sdgs.itb.infrastructure.ranking.dto.RankingSystemDTO;

import java.util.List;

public interface RankingService {
    // Ranking Systems
    List<RankingSystemDTO> getAllRankingSystems();
    RankingSystemDTO createRankingSystem(RankingSystemDTO dto);

    // Ranking Overview
    List<RankingOverviewDTO> getOverviews(Long systemId, Long goalId, Integer year, Boolean publishedOnly);
    RankingOverviewDTO saveOrUpdateOverview(RankingOverviewDTO dto);
    RankingOverviewDTO togglePublishOverview(Long overviewId, Boolean publish);
    void deleteOverview(Long id);

    // Questions & Submissions Tree
    List<RankingQuestionDTO> getQuestionHierarchy(Long systemId, Long goalId, Integer year);
    RankingQuestionDTO createQuestion(RankingQuestionDTO dto);
    RankingQuestionDTO updateQuestion(Long id, RankingQuestionDTO dto);
    void deleteQuestion(Long id);

    // Submission
    RankingSubmissionDTO submitAnswer(RankingSubmissionDTO dto);
}