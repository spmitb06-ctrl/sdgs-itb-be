package com.sdgs.itb.infrastructure.news.repository;

import com.sdgs.itb.entity.news.NewsImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsImageRepository extends JpaRepository<NewsImage, Long> {
    List<NewsImage> findAllByNewsId(Long newsId);
}