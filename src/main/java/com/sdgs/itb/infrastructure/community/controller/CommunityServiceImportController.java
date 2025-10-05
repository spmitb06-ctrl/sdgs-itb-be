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
        importService.importSample(20);
        return ResponseEntity.ok("Imported sample Community Service records");
    }

    @PostMapping("/import/all")
    public ResponseEntity<String> importAll() {
        importService.importAll();
        return ResponseEntity.ok("Imported all Community Service records");
    }
}

