package com.sdgs.itb.service.news;

import java.util.Map;

public interface WordPressNewsSyncService {
    Map<String, Object> syncAllConfiguredSources();
    int syncFromEndpoint(String endpointUrl, String unitAbbreviation);
}