package com.sdgs.itb.infrastructure.news.repository;

import com.sdgs.itb.entity.news.ArticleCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleCategoryRepository extends JpaRepository<ArticleCategory, Long> {
}


