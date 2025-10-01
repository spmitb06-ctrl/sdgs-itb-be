package com.sdgs.itb.infrastructure.news.repository;

import com.sdgs.itb.entity.news.NewsImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsImageRepository extends JpaRepository<NewsImage, Long> {
}

