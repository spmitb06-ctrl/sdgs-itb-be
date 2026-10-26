package com.sdgs.itb.service.news;

import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;

public interface NewsExcelImportService {
    int importNewsFromExcel(MultipartFile file);
    int importNewsFromExcel(InputStream inputStream);
}