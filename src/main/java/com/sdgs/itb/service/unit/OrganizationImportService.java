package com.sdgs.itb.service.unit;

import com.sdgs.itb.infrastructure.typesense.dto.TypesenseOrganizationExportDTO;

public interface OrganizationImportService {
    void importOrUpdateOrganization(TypesenseOrganizationExportDTO dto);
}
