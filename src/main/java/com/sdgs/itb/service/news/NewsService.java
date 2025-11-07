package com.sdgs.itb.service.news;

import com.sdgs.itb.infrastructure.news.dto.NewsDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface NewsService {
    NewsDTO createNews(NewsDTO dto);
    NewsDTO updateNews(Long id, NewsDTO dto);
    void deleteNews(Long id);
    void deleteNewsImage(Long imageId);
    NewsDTO getNews(Long id);
    Page<NewsDTO> getNews(
            String title,
            List<Long> goalIds,
            List<Long> categoryIds,
            List<Long> scholarIds,
            List<Long> unitIds,
            String year,
            String sortBy,
            String sortDir,
            int page,
            int size
    );
}
