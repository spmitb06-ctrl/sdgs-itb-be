package com.sdgs.itb.infrastructure.ranking.controller;

import com.sdgs.itb.common.responses.ApiResponse;
import com.sdgs.itb.infrastructure.ranking.dto.RankingOverviewDTO;
import com.sdgs.itb.infrastructure.ranking.dto.RankingQuestionDTO;
import com.sdgs.itb.infrastructure.ranking.dto.RankingSystemDTO;
import com.sdgs.itb.service.ranking.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rankings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RankingPublicController {

    private final RankingService rankingService;

    @GetMapping("/systems")
    public ResponseEntity<ApiResponse<List<RankingSystemDTO>>> getActiveSystems() {
        return ApiResponse.success("Ranking systems fetched successfully", rankingService.getAllRankingSystems());
    }

    @GetMapping("/overviews")
    public ResponseEntity<ApiResponse<List<RankingOverviewDTO>>> getPublicOverviews(
            @RequestParam Long systemId,
            @RequestParam(required = false) Long goalId,
            @RequestParam Integer year
    ) {
        // Publik hanya mendapatkan data dengan isPublished = true
        List<RankingOverviewDTO> overviews = rankingService.getOverviews(systemId, goalId, year, true);
        return ApiResponse.success("Public ranking overviews fetched successfully", overviews);
    }

    @GetMapping("/questions")
    public ResponseEntity<ApiResponse<List<RankingQuestionDTO>>> getPublicQuestionsHierarchy(
            @RequestParam Long systemId,
            @RequestParam(required = false) Long goalId,
            @RequestParam(required = false) Integer year
    ) {
        List<RankingQuestionDTO> tree = rankingService.getQuestionHierarchy(systemId, goalId, year);
        return ApiResponse.success("Question hierarchy fetched successfully", tree);
    }
}