package com.sdgs.itb.service.news;

import com.sdgs.itb.infrastructure.news.dto.ArticleDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ArticleService {
    ArticleDTO createArticle(ArticleDTO dto);
    ArticleDTO updateArticle(Long id, ArticleDTO dto);
    void deleteArticle(Long id);
    ArticleDTO getArticle(Long id);
    Page<ArticleDTO> getArticles(
            String title,
            List<Long> goalIds,
            List<Long> categoryIds,
            List<Long> scholarIds,
            List<Long> facultyIds,
            String year,
            String sortBy,
            String sortDir,
            int page,
            int size
    );
}
