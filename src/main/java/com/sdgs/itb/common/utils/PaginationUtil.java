package com.sdgs.itb.common.utils;

import com.sdgs.itb.common.responses.PaginatedResponse;
import org.springframework.data.domain.Page;

public class PaginationUtil {
    public static <T> PaginatedResponse<T> toPaginatedResponse(Page<T> page) {
        PaginatedResponse<T> response = new PaginatedResponse<>();
        response.setContent(page.getContent());
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        return response;
    }
}
