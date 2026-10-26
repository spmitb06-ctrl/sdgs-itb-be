package com.sdgs.itb.infrastructure.typesense.dto;

import lombok.Data;
import java.util.List;

@Data
public class TypesenseOrganizationExportDTO {
    private String id;
    private String title;
    private String shortname;
    private String type;
    private String web;
    private String slug;
    private List<String> sdg;

    // Top-level counts
    private Integer cite;
    private Integer paper;
    private Integer patent;
    private Integer project;
    private Integer thesis;
    private Integer outreach;
}