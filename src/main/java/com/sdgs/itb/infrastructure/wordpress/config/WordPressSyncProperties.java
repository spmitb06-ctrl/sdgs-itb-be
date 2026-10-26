package com.sdgs.itb.infrastructure.wordpress.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "wordpress.sync")
public class WordPressSyncProperties {

    private List<WordPressSourceConfig> sources = new ArrayList<>();

    @Data
    public static class WordPressSourceConfig {
        private String name;
        private String url;
        private String unitAbbreviation;
    }
}