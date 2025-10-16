package com.sdgs.itb.infrastructure.typesense.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Data
public class TypesenseNewsExportDTO {
    private String abstractText; // from "abstract"
    private List<String> sdg;    // multiple SDGs
    private String title;
    private String url;
    private LocalDate dateTime;
    private String scholarName;
    private String image;
    private String year;
    private String type;

    private List<Long> organizations;
}

