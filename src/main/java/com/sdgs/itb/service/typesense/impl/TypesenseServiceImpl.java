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
import org.springframework.web.util.UriComponentsBuilder;

import java.io.OutputStream;
import java.net.URI;
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

        URI uri = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("q", "*")
                .queryParam("filter_by", "sdg:=" + sdg)
                .build()
                .encode()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-TYPESENSE-API-KEY", apiKey);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                requestEntity,
                Map.class
        );

        if (response.getBody() != null && response.getBody().get("found") != null) {
            return (int) response.getBody().get("found");
        }
        return 0;
    }

    @Override
    public void importSampleFromTypesense(String collection) {
        importFromTypesenseInternal(collection, 10);
    }

    @Override
    public void importAllFromTypesense(String collection) {
        importFromTypesenseInternal(collection, Integer.MAX_VALUE);
    }

    private void importFromTypesenseInternal(String collection, int importLimit) {
        try {
            String url = baseUrl + "/" + collection + "/documents/search";

            int page = 1;
            int perPage = 200;
            int importedCount = 0;

            while (importedCount < importLimit) {
                URI uri = new URI(url + "?q=*&per_page=" + perPage + "&page=" + page +
                        "&include_fields=abstract,sdg,title,slug,_ts");

                HttpHeaders headers = new HttpHeaders();
                headers.set("X-TYPESENSE-API-KEY", apiKey);
                HttpEntity<Void> request = new HttpEntity<>(headers);

                ResponseEntity<Map> response = restTemplate.exchange(
                        uri,
                        HttpMethod.GET,
                        request,
                        Map.class
                );

                if (response.getBody() == null || response.getBody().get("hits") == null) {
                    break; // no more data
                }

                List<Map<String, Object>> hits = (List<Map<String, Object>>) response.getBody().get("hits");
                if (hits.isEmpty()) {
                    break; // reached end
                }

                for (Map<String, Object> hit : hits) {
                    if (importedCount >= importLimit) {
                        break;
                    }

                    Map<String, Object> doc = (Map<String, Object>) hit.get("document");

                    String abstractText = (String) doc.get("abstract");
                    String title = (String) doc.get("title");
                    String slug = (String) doc.get("slug");
                    List<String> sdg = (List<String>) doc.get("sdg");

                    // ✅ Skip if any field is null or empty
                    if (abstractText == null || abstractText.trim().isEmpty()
                            || title == null || title.trim().isEmpty()
                            || slug == null || slug.trim().isEmpty()
                            || sdg == null || sdg.isEmpty()) {
                        continue;
                    }

                    TypesenseNewsExportDTO dto = new TypesenseNewsExportDTO();
                    dto.setAbstractText(abstractText);
                    dto.setTitle(title);
                    dto.setUrl(slug);
                    dto.setSdg(sdg);
                    dto.setImage("/sdgs/research.jpg");
                    dto.set_ts(((Number) doc.getOrDefault("_ts", 0L)).longValue());
                    dto.setScholarName(collection);

                    newsImportService.importFromTypesense(dto);
                    importedCount++;
                }

                if (hits.size() < perPage) {
                    break; // no more pages
                }

                page++;
            }

            System.out.println("Imported " + importedCount + " records from Typesense [" + collection + "]");
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
//                URI uri = new URI(url + "?q=*&per_page=" + perPage + "&page=" + page + "&include_fields=abstract,sdg,title,url,_ts");
                URI uri = new URI(url + "?q=*&per_page=" + perPage + "&page=" + page);

                HttpHeaders headers = new HttpHeaders();
                headers.set("X-TYPESENSE-API-KEY", apiKey);

                HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

                ResponseEntity<Map> resp = restTemplate.exchange(
                        uri,
                        HttpMethod.GET,
                        requestEntity,
                        Map.class
                );

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

                    // stop if last page
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
