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

            int cutoffYear = 2024;

            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("d MMMM uuuu")
                    .optionalStart()
                    .appendPattern("d MMM uuuu")
                    .optionalEnd()
                    .toFormatter(Locale.ENGLISH);

            while (importedCount < importLimit && hasMore) {
                URI uri;
                if (collection.equals("project") || collection.equals("outreach")) {
                    uri = new URI(url + "?q=*&per_page=" + perPage + "&page=" + page +
                            "&include_fields=abstract,sdg,title,slug,start,year,organization,type");
                } else if (collection.equals("patent")) {
                    uri = new URI(url + "?q=*&per_page=" + perPage + "&page=" + page +
                            "&include_fields=abstract,sdg,title,slug,date,year,organization,type");
                } else {
                    uri = new URI(url + "?q=*&per_page=" + perPage + "&page=" + page +
                            "&include_fields=abstract,sdg,title,slug,year,organization");
                }

                HttpHeaders headers = new HttpHeaders();
                headers.set("X-TYPESENSE-API-KEY", apiKey);
                HttpEntity<Void> request = new HttpEntity<>(headers);

                ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.GET, request, Map.class);

                if (response.getBody() == null || response.getBody().get("hits") == null) break;

                List<Map<String, Object>> hits = (List<Map<String, Object>>) response.getBody().get("hits");
                if (hits.isEmpty()) break;

                for (Map<String, Object> hit : hits) {
                    if (importedCount >= importLimit) break;

                    Map<String, Object> doc = (Map<String, Object>) hit.get("document");
                    if (doc == null) continue;

                    String abstractText = (String) doc.get("abstract");
                    String title = (String) doc.get("title");
                    String slug = (String) doc.get("slug");

                    int year = 0;
                    Object yearObj = doc.get("year");
                    if (yearObj != null) {
                        try {
                            year = Integer.parseInt(String.valueOf(yearObj).trim());
                        } catch (NumberFormatException ignored) {}
                    }
                    if (year < cutoffYear) continue;

                    List<String> sdg = (List<String>) doc.get("sdg");
                    if (sdg != null) {
                        sdg.replaceAll(goal -> {
                            if (goal != null && goal.trim().equalsIgnoreCase("GOAL 16: Peace and Justice Strong Institutions")) {
                                return "GOAL 16: Peace, Justice and Strong Institutions";
                            }
                            return goal;
                        });
                    }

                    String type = (String) doc.get("type");
                    LocalDate eventDate = null;

                    // Parse normal date for specific collections
                    String dateStr = null;
                    if (collection.equals("project")) {
                        dateStr = (String) doc.get("start");
                    } else if (collection.equals("patent")) {
                        dateStr = (String) doc.get("date");
                    }

                    if (dateStr != null && !dateStr.trim().isEmpty()) {
                        try {
                            eventDate = LocalDate.parse(dateStr.trim(), formatter);
                        } catch (DateTimeParseException ignored) {}
                    }

                    // For paper or thesis → set eventDate from Year (1st Jan)
                    if ((collection.equals("paper") || collection.equals("thesis")) && year > 0) {
                        eventDate = LocalDate.of(year, 1, 1);
                    }

                    // Wrap abstractText in <p>...</p> for paper/thesis
                    if ((collection.equals("paper") || collection.equals("thesis")) && abstractText != null && !abstractText.trim().isEmpty()) {
                        String trimmed = abstractText.trim();
                        if (!trimmed.startsWith("<p>")) {
                            abstractText = "<p>" + trimmed + "</p>";
                        }
                    }

                    // Parse organizations (<= 12 only)
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

                    boolean invalid =
                            (title == null || title.trim().isEmpty()) ||
                                    (slug == null || slug.trim().isEmpty()) ||
                                    (sdg == null || sdg.isEmpty()) ||
                                    (!"patent".equals(collection) && (abstractText == null || abstractText.trim().isEmpty()));

                    if (invalid) continue;

                    TypesenseNewsExportDTO dto = new TypesenseNewsExportDTO();
                    dto.setAbstractText(abstractText);
                    dto.setTitle(title);
                    dto.setUrl(slug);
                    dto.setSdg(sdg);
                    dto.setDateTime(eventDate);
                    dto.setScholarName(collection);
                    dto.setYear(String.valueOf(year));
                    dto.setOrganizations(organizations);
                    dto.setType(type);

                    newsImportService.importFromTypesense(dto);
                    importedCount++;
                }

                hasMore = hits.size() >= perPage;
                page++;
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

    @Override
    @SuppressWarnings("unchecked")
    public Set<String> getTypesByCollection(String collection) {
        if (collection == null || collection.trim().isEmpty()) {
            throw new IllegalArgumentException("Collection name must not be empty");
        }

        Set<String> types = new HashSet<>();

        try {
            String url = baseUrl + "/" + collection + "/documents/search";
            int page = 1;
            int perPage = 200;
            boolean hasMore = true;

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-TYPESENSE-API-KEY", apiKey);
            HttpEntity<Void> request = new HttpEntity<>(headers);

            while (hasMore) {
                URI uri = new URI(url + "?q=*&per_page=" + perPage + "&page=" + page + "&include_fields=type");

                ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.GET, request, Map.class);
                if (response.getBody() == null || response.getBody().get("hits") == null) break;

                List<Map<String, Object>> hits = (List<Map<String, Object>>) response.getBody().get("hits");
                if (hits.isEmpty()) break;

                for (Map<String, Object> hit : hits) {
                    Map<String, Object> doc = (Map<String, Object>) hit.get("document");
                    if (doc != null && doc.get("type") != null) {
                        types.add(doc.get("type").toString().trim());
                    }
                }

                int found = ((Number) response.getBody().getOrDefault("found", 0)).intValue();
                if (page * perPage >= found) {
                    hasMore = false;
                } else {
                    page++;
                }
            }

            System.out.println("Found " + types.size() + " unique 'type' values from collection: " + collection);
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching types from collection '" + collection + "': " + e.getMessage(), e);
        }

        return types;
    }
}
