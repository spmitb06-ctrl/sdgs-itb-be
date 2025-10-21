package com.sdgs.itb.service.news;

import com.sdgs.itb.infrastructure.news.dto.NewsCategoryStatsDTO;
import com.sdgs.itb.infrastructure.news.dto.NewsGoalStatsDTO;

import java.util.List;

public interface NewsStatsService {
    List<NewsGoalStatsDTO> getNewsStatsByGoal(Integer year, Long categoryId);
    List<NewsCategoryStatsDTO> getNewsStatsByCategory(Integer year, Long goalId);
}

