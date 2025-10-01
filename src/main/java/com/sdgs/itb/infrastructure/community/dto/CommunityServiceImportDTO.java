package com.sdgs.itb.infrastructure.community.dto;

import lombok.Data;

import java.util.List;

@Data
public class CommunityServiceImportDTO {
    private String title;
    private String content; // from "contex"
    private String image; // url_image
    private String sourceUrl; // generated URL
    private String createdDate; // from created_date
    private List<String> sdgs; // extracted SDGs
}

