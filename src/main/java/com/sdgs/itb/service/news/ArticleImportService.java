package com.sdgs.itb.service.news;

import com.sdgs.itb.entity.news.Article;
import com.sdgs.itb.infrastructure.typesense.dto.TypesenseArticleExportDTO;

public interface ArticleImportService {
    Article importFromTypesense(TypesenseArticleExportDTO dto);
}
