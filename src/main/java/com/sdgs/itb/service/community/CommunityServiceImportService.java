package com.sdgs.itb.service.community;

import com.sdgs.itb.infrastructure.community.dto.CommunityServiceImportDTO;

public interface CommunityServiceImportService {

    void importSample(int limit); // import sample, e.g., 10 records

    void importAll(); // import all records

    void importFromDTO(CommunityServiceImportDTO dto); // import single DTO
}

