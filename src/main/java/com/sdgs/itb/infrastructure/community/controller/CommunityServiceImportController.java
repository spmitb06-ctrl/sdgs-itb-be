package com.sdgs.itb.infrastructure.community.controller;

import com.sdgs.itb.service.community.CommunityServiceImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/community-service")
@RequiredArgsConstructor
public class CommunityServiceImportController {

    private final CommunityServiceImportService importService;

    @PostMapping("/import/sample")
    public ResponseEntity<String> importSample() {
        int count = importService.importSample(20);
        return ResponseEntity.ok("Imported " + count + " new Community Service records.");
    }

    @PostMapping("/import/all")
    public ResponseEntity<String> importAll() {
        int count = importService.importAll();
        return ResponseEntity.ok("Imported " + count + " new Community Service records.");
    }

    @PostMapping("/migrate/source-url")
    public ResponseEntity<String> migrateSourceUrl() {
        int count = importService.migrateSourceUrlAndSlug();
        return ResponseEntity.ok("Migrated " + count + " Community Service records.");
    }

    @PostMapping("/migrate/hashtag-goals")
    public ResponseEntity<String> migrateHashtagGoals() {
        int count = importService.migrateHashtagGoals();
        return ResponseEntity.ok(
                "Updated hashtagGoals for " + count + " news records."
        );
    }
}
