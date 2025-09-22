package com.sdgs.itb.infrastructure.news.repository;

import com.sdgs.itb.entity.news.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long>, JpaSpecificationExecutor<Article> {
    @Query("SELECT a FROM Article a WHERE LOWER(a.sourceUrl) = LOWER(:url)")
    Optional<Article> findBySourceUrl(@Param("url") String url);

    @Query("SELECT a FROM Article a WHERE LOWER(a.title) = LOWER(:title)")
    Optional<Article> findByTitleIgnoreCase(@Param("title") String title);
}
