package com.sdgs.itb.infrastructure.news.mapper;

import com.sdgs.itb.entity.unit.Unit;
import com.sdgs.itb.entity.news.News;
import com.sdgs.itb.entity.news.NewsUnit;
import com.sdgs.itb.entity.news.NewsGoal;
import com.sdgs.itb.entity.news.NewsImage;
import com.sdgs.itb.entity.goal.Goal;
import com.sdgs.itb.infrastructure.unit.dto.UnitDTO;
import com.sdgs.itb.infrastructure.news.dto.NewsDTO;
import com.sdgs.itb.infrastructure.news.dto.NewsUnitDTO;
import com.sdgs.itb.infrastructure.news.dto.NewsGoalDTO;
import com.sdgs.itb.infrastructure.goal.dto.GoalDTO;

import java.util.stream.Collectors;

public class NewsMapper {
    public static News toEntity(NewsDTO dto) {
        News news = new News();
        news.setTitle(dto.getTitle());
        news.setContent(dto.getContent());
        news.setThumbnailUrl(dto.getThumbnailUrl());
        news.setSourceUrl(dto.getSourceUrl());
        news.setEventDate(dto.getEventDate());

        if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
            news.setImages(dto.getImageUrls().stream()
                    .map(url -> NewsImage.builder()
                            .imageUrl(url)
                            .news(news)
                            .build())
                    .collect(Collectors.toList()));
        }

        return news;
    }

    public static NewsDTO toDTO(News news) {
        NewsDTO dto = new NewsDTO();
        dto.setId(news.getId());
        dto.setTitle(news.getTitle());
        dto.setContent(news.getContent());
        dto.setThumbnailUrl(news.getThumbnailUrl());
        dto.setSourceUrl(news.getSourceUrl());
        dto.setEventDate(news.getEventDate());
        dto.setCreatedAt(news.getCreatedAt());

        dto.setImageUrls(news.getImages().stream()
                .map(NewsImage::getImageUrl)
                .collect(Collectors.toList()));

        dto.setNewsUnits(news.getNewsUnits().stream()
                .map(NewsMapper::toNewsUnitDTO)
                .collect(Collectors.toList()));

        dto.setNewsGoals(news.getNewsGoals().stream()
                .map(NewsMapper::toNewsGoalDTO)
                .collect(Collectors.toList()));

        if (news.getNewsCategory() != null) {
            dto.setCategoryId(news.getNewsCategory().getId());
            dto.setCategoryName(news.getNewsCategory().getCategory());
        }

        if (news.getScholar() != null) {
            dto.setScholarId(news.getScholar().getId());
            dto.setScholarName(news.getScholar().getName());
        }

        return dto;
    }

    public static UnitDTO toUnitDTO(Unit unit) {
        UnitDTO dto = new UnitDTO();
        dto.setId(unit.getId());
        dto.setName(unit.getName());
        return dto;
    }

    public static GoalDTO toGoalDTO(Goal goal) {
        GoalDTO dto = new GoalDTO();
        dto.setId(goal.getId());
        dto.setGoalNumber(goal.getGoalNumber());
        dto.setTitle(goal.getTitle());
        dto.setDescription(goal.getDescription());
        dto.setColor(goal.getColor());
        dto.setIcon(goal.getIcon());
        dto.setEditedIcon(goal.getEditedIcon());
        dto.setLinkUrl(goal.getLinkUrl());
        return dto;
    }

    public static NewsUnitDTO toNewsUnitDTO(NewsUnit newsUnit) {
        NewsUnitDTO dto = new NewsUnitDTO();
        dto.setId(newsUnit.getId());
        dto.setUnits(toUnitDTO(newsUnit.getUnit()));
        return dto;
    }

    public static NewsGoalDTO toNewsGoalDTO(NewsGoal newsGoal) {
        NewsGoalDTO dto = new NewsGoalDTO();
        dto.setId(newsGoal.getId());
        dto.setGoals(toGoalDTO(newsGoal.getGoal()));
        return dto;
    }
}

