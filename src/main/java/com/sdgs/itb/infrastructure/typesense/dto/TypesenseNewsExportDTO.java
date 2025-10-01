package com.sdgs.itb.infrastructure.typesense.dto;

import lombok.Data;

import java.util.List;

@Data
public class TypesenseNewsExportDTO {
    private String abstractText; // from "abstract"
    private List<String> sdg;    // multiple SDGs
    private String title;
    private String url;
    private Long _ts;
    private String scholarName;
    private String image;
}

