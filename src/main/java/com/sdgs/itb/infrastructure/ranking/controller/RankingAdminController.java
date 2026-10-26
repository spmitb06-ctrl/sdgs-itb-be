package com.sdgs.itb.infrastructure.ranking.controller;

import com.sdgs.itb.common.responses.ApiResponse;
import com.sdgs.itb.infrastructure.ranking.dto.RankingOverviewDTO;
import com.sdgs.itb.infrastructure.ranking.dto.RankingQuestionDTO;
import com.sdgs.itb.infrastructure.ranking.dto.RankingSubmissionDTO;
import com.sdgs.itb.infrastructure.ranking.dto.RankingSystemDTO;
import com.sdgs.itb.service.ranking.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/rankings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasAnyAuthority('ADMIN DEV', 'ADMIN WEB')")
public class RankingAdminController {

    private final RankingService rankingService;

    // --- Ranking Systems ---
    @PostMapping("/systems")
    public ResponseEntity<ApiResponse<RankingSystemDTO>> createSystem(@RequestBody RankingSystemDTO dto) {
        return ApiResponse.success(HttpStatus.CREATED.value(), "Ranking system created successfully", rankingService.createRankingSystem(dto));
    }

    // --- Ranking Overviews (Draft & Published) ---
    @GetMapping("/overviews")
    public ResponseEntity<ApiResponse<List<RankingOverviewDTO>>> getAdminOverviews(
            @RequestParam(required = false) Long systemId,
            @RequestParam(required = false) Long goalId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Boolean isPublished
    ) {
        // Admin dapat melihat seluruh data (draft dan publish) sesuai filter
        List<RankingOverviewDTO> overviews = rankingService.getOverviews(systemId, goalId, year, isPublished);
        return ApiResponse.success("Admin ranking overviews fetched successfully", overviews);
    }

    @PostMapping("/overviews")
    public ResponseEntity<ApiResponse<RankingOverviewDTO>> saveOrUpdateOverview(@RequestBody RankingOverviewDTO dto) {
        return ApiResponse.success("Overview saved successfully", rankingService.saveOrUpdateOverview(dto));
    }

    @PatchMapping("/overviews/{id}/publish")
    public ResponseEntity<ApiResponse<RankingOverviewDTO>> togglePublish(
            @PathVariable Long id,
            @RequestParam Boolean publish
    ) {
        return ApiResponse.success("Publication status updated", rankingService.togglePublishOverview(id, publish));
    }

    @DeleteMapping("/overviews/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOverview(@PathVariable Long id) {
        rankingService.deleteOverview(id);
        return ApiResponse.success("Overview deleted successfully", null);
    }

    // --- Questions Bank ---
    @PostMapping("/questions")
    public ResponseEntity<ApiResponse<RankingQuestionDTO>> createQuestion(@RequestBody RankingQuestionDTO dto) {
        return ApiResponse.success(HttpStatus.CREATED.value(), "Question created successfully", rankingService.createQuestion(dto));
    }

    @PutMapping("/questions/{id}")
    public ResponseEntity<ApiResponse<RankingQuestionDTO>> updateQuestion(
            @PathVariable Long id,
            @RequestBody RankingQuestionDTO dto
    ) {
        return ApiResponse.success("Question updated successfully", rankingService.updateQuestion(id, dto));
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(@PathVariable Long id) {
        rankingService.deleteQuestion(id);
        return ApiResponse.success("Question deleted successfully", null);
    }

    // --- Submissions ---
    @PostMapping("/submissions")
    public ResponseEntity<ApiResponse<RankingSubmissionDTO>> submitAnswer(@RequestBody RankingSubmissionDTO dto) {
        return ApiResponse.success("Submission saved successfully", rankingService.submitAnswer(dto));
    }
}