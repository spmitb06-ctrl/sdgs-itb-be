package com.sdgs.itb.infrastructure.news.repository;

import com.sdgs.itb.entity.news.NewsCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsCategoryRepository extends JpaRepository<NewsCategory, Long> {
}


