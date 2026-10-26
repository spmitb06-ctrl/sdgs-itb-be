package com.sdgs.itb.infrastructure.wordpress.controller;

import com.sdgs.itb.service.news.WordPressNewsSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/news/wordpress")
@RequiredArgsConstructor
public class WordPressNewsSyncController {

    private final WordPressNewsSyncService wordPressNewsSyncService;

    @PostMapping("/sync-all")
    public ResponseEntity<?> syncAll() {
        Map<String, Object> result = wordPressNewsSyncService.syncAllConfiguredSources();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", result
        ));
    }

    @PostMapping("/sync-custom")
    public ResponseEntity<?> syncCustom(
            @RequestParam("url") String url,
            @RequestParam(value = "unit", defaultValue = "FITB") String unitAbbreviation
    ) {
        int count = wordPressNewsSyncService.syncFromEndpoint(url, unitAbbreviation);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Successfully synced " + count + " news items from " + url,
                "importedCount", count
        ));
    }
}