package com.sdgs.itb.infrastructure.news.repository;

import com.sdgs.itb.entity.news.ArticleImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleImageRepository extends JpaRepository<ArticleImage, Long> {
}

