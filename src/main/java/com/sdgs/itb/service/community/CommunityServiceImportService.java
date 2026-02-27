package com.sdgs.itb.service.community;

import com.sdgs.itb.infrastructure.community.dto.CommunityServiceImportDTO;

public interface CommunityServiceImportService {

    int importSample(int limit); // import sample, e.g., 10 records

    int importAll(); // import all records

    void importFromDTO(CommunityServiceImportDTO dto);

    int migrateSourceUrlAndSlug();

    int migrateHashtagGoals();

    int migrateSlug();
}

