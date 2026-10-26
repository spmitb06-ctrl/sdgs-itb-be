package com.sdgs.itb.service.news.impl;

import com.sdgs.itb.entity.goal.Goal;
import com.sdgs.itb.entity.goal.Scholar;
import com.sdgs.itb.entity.news.News;
import com.sdgs.itb.entity.news.NewsCategory;
import com.sdgs.itb.entity.unit.Unit;
import com.sdgs.itb.infrastructure.goal.repository.GoalRepository;
import com.sdgs.itb.infrastructure.goal.repository.ScholarRepository;
import com.sdgs.itb.infrastructure.news.dto.NewsExcelRowDTO;
import com.sdgs.itb.infrastructure.news.repository.NewsCategoryRepository;
import com.sdgs.itb.infrastructure.news.repository.NewsRepository;
import com.sdgs.itb.infrastructure.unit.repository.UnitRepository;
import com.sdgs.itb.service.news.NewsExcelImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsExcelImportServiceImpl implements NewsExcelImportService {

    private static final String DEFAULT_THUMBNAIL_URL = "/news/pt_lapi_itb_sdg_17.jpg";
    private static final Long DEFAULT_NEWS_CATEGORY_ID = 1L;
    private static final Long DEFAULT_SCHOLAR_ID = 4L;

    private final NewsRepository newsRepository;
    private final UnitRepository unitRepository;
    private final GoalRepository goalRepository;
    private final NewsCategoryRepository newsCategoryRepository;
    private final ScholarRepository scholarRepository;

    private final Map<String, Unit> unitByAbbrCache = new HashMap<>();
    private Goal goal17Cache;
    private NewsCategory defaultCategoryCache;
    private Scholar defaultScholarCache;

    private synchronized void initCaches() {
        // 1. Resolve Goal 17
        if (goal17Cache == null) {
            goal17Cache = goalRepository.findAll().stream()
                    .filter(g -> (g.getGoalNumber() != null && g.getGoalNumber() == 17)
                            || (g.getTitle() != null && g.getTitle().toLowerCase().contains("goal 17")))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Goal 17 not found in sdgs.goals"));
        }

        // 2. Pre-cache all active Units by abbreviation
        if (unitByAbbrCache.isEmpty()) {
            unitRepository.findAll().forEach(u -> {
                if (u.getAbbreviation() != null && !u.getAbbreviation().isBlank() && u.getDeletedAt() == null) {
                    unitByAbbrCache.put(u.getAbbreviation().toLowerCase().trim(), u);
                }
            });
        }

        // 3. Resolve Category ID = 1
        if (defaultCategoryCache == null) {
            defaultCategoryCache = newsCategoryRepository.findById(DEFAULT_NEWS_CATEGORY_ID)
                    .orElseThrow(() -> new IllegalStateException("NewsCategory with ID 1 not found in sdgs.news_categories"));
        }

        // 4. Resolve Scholar ID = 4
        if (defaultScholarCache == null) {
            defaultScholarCache = scholarRepository.findById(DEFAULT_SCHOLAR_ID)
                    .orElseThrow(() -> new IllegalStateException("Scholar with ID 4 not found in sdgs.scholars"));
        }
    }

    @Override
    @Transactional
    public int importNewsFromExcel(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            return importNewsFromExcel(is);
        } catch (Exception e) {
            log.error("Failed to parse Excel file", e);
            throw new RuntimeException("Error processing Excel file: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public int importNewsFromExcel(InputStream inputStream) {
        initCaches();

        List<NewsExcelRowDTO> rows = parseExcel(inputStream);
        int importedCount = 0;

        for (NewsExcelRowDTO rowDto : rows) {
            String title = rowDto.getTitle();
            if (title == null || title.isBlank()) {
                continue;
            }

            Optional<News> existing = newsRepository.findByTitleIgnoreCase(title.trim())
                    .stream()
                    .findFirst();

            News news = existing.orElseGet(News::new);

            news.setTitle(title.trim());
            news.setContent(rowDto.getContent());
            news.setScholarYear(rowDto.getYear());
            news.setThumbnailUrl(DEFAULT_THUMBNAIL_URL);
            news.setNewsCategory(defaultCategoryCache);
            news.setScholar(defaultScholarCache);

            // Generate slug if null or empty
            if (news.getSlug() == null || news.getSlug().isBlank()) {
                String generatedSlug = title.trim().toLowerCase()
                        .replaceAll("[^a-z0-9\\s-]", "")
                        .replaceAll("\\s+", "-");
                news.setSlug(generatedSlug);
            }

            // Set fallback sourceUrl if not present
            if (news.getSourceUrl() == null || news.getSourceUrl().isBlank()) {
                news.setSourceUrl("https://itb.ac.id");
            }

            if (news.getEventDate() == null) {
                try {
                    int y = Integer.parseInt(rowDto.getYear().trim());
                    news.setEventDate(LocalDate.of(y, 1, 1));
                } catch (Exception e) {
                    news.setEventDate(LocalDate.now());
                }
            }

            // Link Goal #17
            news.addGoal(goal17Cache);

            // Link Unit matching abbreviation
            String abbr = rowDto.getUnitAbbreviation();
            if (abbr != null && !abbr.isBlank()) {
                Unit matchedUnit = unitByAbbrCache.get(abbr.toLowerCase().trim());
                if (matchedUnit == null) {
                    matchedUnit = unitRepository.findByAbbreviation(abbr.trim()).orElse(null);
                    if (matchedUnit != null) {
                        unitByAbbrCache.put(abbr.toLowerCase().trim(), matchedUnit);
                    }
                }

                if (matchedUnit != null) {
                    news.addUnit(matchedUnit);
                } else {
                    log.warn("Unit abbreviation '{}' not found in database for news: {}", abbr, title);
                }
            }

            newsRepository.save(news);
            importedCount++;
        }

        log.info("Successfully imported {} news records from Excel", importedCount);
        return importedCount;
    }

    private List<NewsExcelRowDTO> parseExcel(InputStream is) {
        List<NewsExcelRowDTO> list = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();

        try (Workbook workbook = new XSSFWorkbook(is)) {
            for (int s = 0; s < workbook.getNumberOfSheets(); s++) {
                Sheet sheet = workbook.getSheetAt(s);
                if (sheet == null || sheet.getLastRowNum() < 1) {
                    continue;
                }

                int headerRowIdx = -1;
                int yearCol = -1;
                int titleCol = -1;
                int contentCol = -1;
                int unitCol = -1;

                for (int r = 0; r <= Math.min(5, sheet.getLastRowNum()); r++) {
                    Row row = sheet.getRow(r);
                    if (row == null) continue;

                    int tempYear = -1, tempTitle = -1, tempContent = -1, tempUnit = -1;
                    for (Cell cell : row) {
                        String val = formatter.formatCellValue(cell).toLowerCase().trim();
                        if (val.contains("year") || val.contains("tahun")) tempYear = cell.getColumnIndex();
                        else if (val.contains("title") || val.contains("judul")) tempTitle = cell.getColumnIndex();
                        else if (val.contains("content") || val.contains("isi") || val.contains("deskripsi")) tempContent = cell.getColumnIndex();
                        else if (val.contains("unit") || val.contains("fakultas") || val.contains("abbr")) tempUnit = cell.getColumnIndex();
                    }

                    if (tempTitle != -1 && (tempYear != -1 || tempContent != -1)) {
                        headerRowIdx = r;
                        yearCol = tempYear;
                        titleCol = tempTitle;
                        contentCol = tempContent;
                        unitCol = tempUnit;
                        break;
                    }
                }

                if (headerRowIdx == -1) {
                    continue;
                }

                for (int r = headerRowIdx + 1; r <= sheet.getLastRowNum(); r++) {
                    Row row = sheet.getRow(r);
                    if (row == null) continue;

                    String year = yearCol != -1 ? formatter.formatCellValue(row.getCell(yearCol)).trim() : null;
                    if (year != null && year.endsWith(".0")) {
                        year = year.substring(0, year.length() - 2);
                    }

                    String title = titleCol != -1 ? formatter.formatCellValue(row.getCell(titleCol)).trim() : null;
                    String content = contentCol != -1 ? formatter.formatCellValue(row.getCell(contentCol)).trim() : null;
                    String unit = unitCol != -1 ? formatter.formatCellValue(row.getCell(unitCol)).trim() : null;

                    if (title != null && !title.isBlank()) {
                        list.add(NewsExcelRowDTO.builder()
                                .year(year)
                                .title(title)
                                .content(content)
                                .unitAbbreviation(unit)
                                .build());
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading Excel cells: " + e.getMessage(), e);
        }

        return list;
    }
}