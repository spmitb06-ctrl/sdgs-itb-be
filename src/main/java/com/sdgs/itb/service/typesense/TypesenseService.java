package com.sdgs.itb.service.typesense;

import jakarta.servlet.http.HttpServletResponse;

public interface TypesenseService {
    int searchCount(String collection, String sdg);
    void importSampleFromTypesense(int limit, String collection);
    void importAllFromTypesense(String collection);
    void streamExport(String collection, HttpServletResponse response);
}
