package com.sdgs.itb.infrastructure.news.repository;

import com.sdgs.itb.entity.news.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NewsRepository extends JpaRepository<News, Long>, JpaSpecificationExecutor<News> {
    @Query("SELECT a FROM News a WHERE LOWER(a.sourceUrl) = LOWER(:url)")
    Optional<News> findBySourceUrl(@Param("url") String url);

    @Query("SELECT a FROM News a WHERE LOWER(a.title) = LOWER(:title)")
    Optional<News> findByTitleIgnoreCase(@Param("title") String title);
}
