package com.sdgs.itb.service.typesense.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdgs.itb.infrastructure.typesense.dto.TypesenseNewsExportDTO;
import com.sdgs.itb.service.news.NewsImportService;
import com.sdgs.itb.service.typesense.TypesenseService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.OutputStream;
import java.net.URI;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TypesenseServiceImpl implements TypesenseService {

    private final RestTemplate restTemplate;
    private final NewsImportService newsImportService;

    @Value("${typesense.apiKey}")
    private String apiKey;

    @Value("${typesense.baseUrl}")
    private String baseUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public int searchCount(String collection, String sdg) {
        String url = baseUrl + "/" + collection + "/documents/search";
        URI uri = URI.create(url + "?q=*&filter_by=sdg:=" + sdg);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-TYPESENSE-API-KEY", apiKey);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                uri, HttpMethod.GET, requestEntity, Map.class
        );

        if (response.getBody() != null && response.getBody().get("found") != null) {
            return (int) response.getBody().get("found");
        }
        return 0;
    }

    @Override
    public void importSampleFromTypesense(int limit, String collection) {
        int safeLimit = (limit > 0) ? limit : 10;
        importFromTypesenseInternal(collection, safeLimit);
    }

    @Override
    public void importAllFromTypesense(String collection) {
        importFromTypesenseInternal(collection, Integer.MAX_VALUE);
    }

    @SuppressWarnings("unchecked")
    private void importFromTypesenseInternal(String collection, int importLimit) {
        try {
            String url = baseUrl + "/" + collection + "/documents/search";

            int page = 1;
            int perPage = 200;
            int importedCount = 0;
            boolean hasMore = true;

            // cutoff timestamp = 2024-01-01 UTC
            LocalDate cutoffDate = LocalDate.of(2024, 1, 1);

            // formatter for "start" field in project collection (e.g. "5 January 2024")
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("d MMMM uuuu")   // 5 January 2024
                    .optionalStart()
                    .appendPattern("d MMM uuuu")    // 5 Jan 2024
                    .optionalEnd()
                    .toFormatter(Locale.ENGLISH);

            while (importedCount < importLimit && hasMore) {
                URI uri;
                if (collection.equals("project")) {
                    uri = new URI(url + "?q=*&per_page=" + perPage + "&page=" + page +
                            "&include_fields=abstract,sdg,title,slug,start,year,organization");
                } else {
                    uri = new URI(url + "?q=*&per_page=" + perPage + "&page=" + page +
                            "&include_fields=abstract,sdg,title,slug,_ts,year,organization");
                }

                HttpHeaders headers = new HttpHeaders();
                headers.set("X-TYPESENSE-API-KEY", apiKey);
                HttpEntity<Void> request = new HttpEntity<>(headers);

                ResponseEntity<Map> response = restTemplate.exchange(
                        uri, HttpMethod.GET, request, Map.class
                );

                if (response.getBody() == null || response.getBody().get("hits") == null) {
                    break; // no more data
                }

                List<Map<String, Object>> hits = (List<Map<String, Object>>) response.getBody().get("hits");
                if (hits.isEmpty()) break;

                for (Map<String, Object> hit : hits) {
                    if (importedCount >= importLimit) break;

                    Map<String, Object> doc = (Map<String, Object>) hit.get("document");
                    if (doc == null) continue;

                    String abstractText = (String) doc.get("abstract");
                    String title = (String) doc.get("title");
                    String slug = (String) doc.get("slug");

                    Object yearObj = doc.get("year");
                    String year = (yearObj != null) ? String.valueOf(yearObj) : null;

                    List<String> sdg = (List<String>) doc.get("sdg");

                    // ✅ Parse LocalDate instead of OffsetDateTime
                    LocalDate eventDate = null;
                    if (collection.equals("project")) {
                        String startStr = (String) doc.get("start");
                        if (startStr == null || startStr.trim().isEmpty()) continue;
                        try {
                            LocalDate parsed = LocalDate.parse(startStr.trim(), formatter);
                            if (parsed.isBefore(cutoffDate)) continue;
                            eventDate = parsed;
                        } catch (DateTimeParseException e) {
                            System.out.println("Skipping invalid start date: " + startStr);
                            continue;
                        }
                    } else {
                        Long ts = ((Number) doc.getOrDefault("_ts", 0L)).longValue();
                        if (ts == 0L) continue;
                        eventDate = Instant.ofEpochSecond(ts).atZone(ZoneOffset.UTC).toLocalDate();
                        if (eventDate.isBefore(cutoffDate)) continue;
                    }

                    // ✅ Parse organizations (only <= 12)
                    List<String> orgStrings = (List<String>) doc.get("organization");
                    List<Long> organizations = new ArrayList<>();
                    if (orgStrings != null) {
                        for (String org : orgStrings) {
                            try {
                                long orgId = Long.parseLong(org);
                                if (orgId <= 12) organizations.add(orgId);
                            } catch (NumberFormatException ignored) {}
                        }
                    }

                    // ✅ Skip invalid entries
                    if (abstractText == null || abstractText.trim().isEmpty()
                            || title == null || title.trim().isEmpty()
                            || slug == null || slug.trim().isEmpty()
                            || sdg == null || sdg.isEmpty()) {
                        continue;
                    }

                    // ✅ Build DTO
                    TypesenseNewsExportDTO dto = new TypesenseNewsExportDTO();
                    dto.setAbstractText(abstractText);
                    dto.setTitle(title);
                    dto.setUrl(slug);
                    dto.setSdg(sdg);
                    dto.setDateTime(eventDate);  // <-- LocalDate
                    dto.setScholarName(collection);
                    dto.setYear(year);
                    dto.setOrganizations(organizations);

                    // ✅ Import via service
                    newsImportService.importFromTypesense(dto);
                    importedCount++;
                }

                if (hits.size() < perPage) {
                    hasMore = false;
                } else {
                    page++;
                }
            }

            System.out.println("✅ Imported " + importedCount + " records from Typesense [" + collection + "]");
        } catch (Exception e) {
            throw new RuntimeException("Error while import: " + e.getMessage(), e);
        }
    }

    @Override
    public void streamExport(String collection, HttpServletResponse response) {
        try {
            String url = baseUrl + "/" + collection + "/documents/search";

            int page = 1;
            int perPage = 200;
            boolean hasMore = true;

            response.setContentType("application/x-ndjson");
            response.setHeader("Content-Disposition", "attachment; filename=" + collection + "-export.ndjson");

            OutputStream out = response.getOutputStream();

            while (hasMore) {
                URI uri = new URI(url + "?q=*&per_page=" + perPage + "&page=" + page);

                HttpHeaders headers = new HttpHeaders();
                headers.set("X-TYPESENSE-API-KEY", apiKey);

                HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

                ResponseEntity<Map> resp = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, Map.class);

                if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                    Map body = resp.getBody();
                    List<Map<String, Object>> hits = (List<Map<String, Object>>) body.get("hits");
                    if (hits == null || hits.isEmpty()) {
                        hasMore = false;
                        break;
                    }

                    for (Map<String, Object> hit : hits) {
                        Map<String, Object> document = (Map<String, Object>) hit.get("document");
                        out.write(objectMapper.writeValueAsBytes(document));
                        out.write("\n".getBytes());
                    }

                    int found = (int) body.get("found");
                    if (page * perPage >= found) {
                        hasMore = false;
                    }
                } else {
                    hasMore = false;
                }

                page++;
            }

            out.flush();
            out.close();

        } catch (Exception e) {
            throw new RuntimeException("Error while streaming export: " + e.getMessage(), e);
        }
    }
}
