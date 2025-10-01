package com.sdgs.itb.infrastructure.goal.controller;

import com.sdgs.itb.common.responses.ApiResponse;
import com.sdgs.itb.infrastructure.goal.dto.GoalScholarDTO;
import com.sdgs.itb.infrastructure.goal.dto.GoalScholarRequest;
import com.sdgs.itb.service.goal.GoalScholarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/goal-scholars")
@CrossOrigin(origins = "*")
public class GoalScholarController {

    private final GoalScholarService goalScholarService;

    @GetMapping("/{goalId}")
    public ResponseEntity<ApiResponse<List<GoalScholarDTO>>> getByGoal(@PathVariable Long goalId) {
        List<GoalScholarDTO> scholars = goalScholarService.getByGoal(goalId);
        return ApiResponse.success(
                HttpStatus.OK.value(),
                "GoalScholars fetched successfully",
                scholars
        );
    }

    @GetMapping("/{goalId}/{scholarId}")
    public ResponseEntity<ApiResponse<GoalScholarDTO>> getOne(
            @PathVariable Long goalId,
            @PathVariable Long scholarId
    ) {
        GoalScholarDTO gs = goalScholarService.getOne(goalId, scholarId);
        return ApiResponse.success(
                HttpStatus.OK.value(),
                "GoalScholar fetched successfully",
                gs
        );
    }

    @PostMapping("/update/counts")
    public ResponseEntity<ApiResponse<String>> updateCounts() {
        goalScholarService.updateAllCounts();
        return ApiResponse.success(
                HttpStatus.OK.value(),
                "GoalScholar counts updated successfully",
                "Done"
        );
    }

    @PutMapping
    public ResponseEntity<ApiResponse<GoalScholarDTO>> updateLink(@RequestBody GoalScholarRequest request) {
        GoalScholarDTO updated = goalScholarService.updateLink(request);
        return ApiResponse.success(
                HttpStatus.OK.value(),
                "GoalScholar link updated successfully",
                updated
        );
    }
}
