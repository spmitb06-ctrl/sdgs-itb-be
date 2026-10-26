package com.sdgs.itb.infrastructure.wordpress.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WordPressPostDTO {

    private Long id;
    private String date;
    private String link;
    private String slug;

    private RenderedField title;
    private RenderedField excerpt;
    private RenderedField content;

    @JsonProperty("class_list")
    private List<String> classList;

    @JsonProperty("_embedded")
    private EmbeddedData embedded;

    @JsonProperty("_links")
    private Map<String, List<LinkItem>> links;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RenderedField {
        private String rendered;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LinkItem {
        private String href;
        private String taxonomy;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EmbeddedData {
        @JsonProperty("wp:featuredmedia")
        private List<FeaturedMedia> featuredMedia;

        @JsonProperty("wp:term")
        private List<List<TermItem>> terms;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FeaturedMedia {
        @JsonProperty("source_url")
        private String sourceUrl;

        private RenderedField guid;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TermItem {
        private Long id;
        private String name;
        private String slug;
        private String taxonomy;
    }
}