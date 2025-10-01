package com.sdgs.itb.service.news;

import com.sdgs.itb.entity.news.News;
import com.sdgs.itb.infrastructure.typesense.dto.TypesenseNewsExportDTO;

public interface NewsImportService {
    News importFromTypesense(TypesenseNewsExportDTO dto);
}
