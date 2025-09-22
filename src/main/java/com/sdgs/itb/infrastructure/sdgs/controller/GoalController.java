package com.sdgs.itb.infrastructure.sdgs.controller;

import com.sdgs.itb.common.responses.ApiResponse;
import com.sdgs.itb.infrastructure.sdgs.dto.GoalDTO;
import com.sdgs.itb.service.sdgs.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/goals")
@CrossOrigin(origins = "*")
public class GoalController {

    private final GoalService goalService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<GoalDTO>>> getAll() {
        List<GoalDTO> goals = goalService.getAllGoals();
        return ApiResponse.success(
                HttpStatus.OK.value(),
                "Goals fetched successfully",
                goals
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GoalDTO>> getOne(@PathVariable Long id) {
        try {
            GoalDTO goal = goalService.getGoal(id);
            return ApiResponse.success(
                    HttpStatus.OK.value(),
                    "Goal fetched successfully",
                    goal
            );
        } catch (Exception e) {
            return ApiResponse.failed(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage()
            );
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GoalDTO>> create(@RequestBody GoalDTO goalDTO) {
        GoalDTO created = goalService.createGoal(goalDTO);
        return ApiResponse.success(
                HttpStatus.CREATED.value(),
                "Goal created successfully",
                created
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GoalDTO>> update(@PathVariable Long id, @RequestBody GoalDTO goalDTO) {
        GoalDTO updated = goalService.updateGoal(id, goalDTO);
        return ApiResponse.success(
                HttpStatus.OK.value(),
                "Goal updated successfully",
                updated
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        goalService.deleteGoal(id);
        return ApiResponse.success(
                HttpStatus.NO_CONTENT.value(),
                "Goal deleted successfully",
                null
        );
    }
}
