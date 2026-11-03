package com.sdgs.itb.service.news;

import com.sdgs.itb.infrastructure.news.dto.NewsCategoryStatsDTO;
import com.sdgs.itb.infrastructure.news.dto.NewsGoalStatsDTO;

import java.time.LocalDate;
import java.util.List;

public interface NewsStatsService {
    List<NewsGoalStatsDTO> getNewsStatsByGoal(Integer year, Long categoryId, List<Long> scholarIds);
    List<NewsCategoryStatsDTO> getNewsStatsByCategory(Integer year, Long goalId);
    List<NewsCategoryStatsDTO> getNewsStatsByCategory(Integer year, Long goalId, LocalDate startDate, LocalDate endDate);
}