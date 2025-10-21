package com.sdgs.itb.service.news.impl;

import com.sdgs.itb.infrastructure.news.dto.NewsCategoryStatsDTO;
import com.sdgs.itb.infrastructure.news.dto.NewsGoalStatsDTO;
import com.sdgs.itb.infrastructure.news.repository.NewsCategoryRepository;
import com.sdgs.itb.infrastructure.news.repository.NewsGoalRepository;
import com.sdgs.itb.service.news.NewsStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsStatsServiceImpl implements NewsStatsService {

    private final NewsGoalRepository newsGoalRepository;
    private final NewsCategoryRepository newsCategoryRepository;

    @Override
    public List<NewsGoalStatsDTO> getNewsStatsByGoal(Integer year, Long categoryId) {
        return newsGoalRepository.findNewsCountByGoal(year, categoryId);
    }

    @Override
    public List<NewsCategoryStatsDTO> getNewsStatsByCategory(Integer year, Long goalId) {
        return newsCategoryRepository.findNewsCountByCategory(year, goalId);
    }
}
