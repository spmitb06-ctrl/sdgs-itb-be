package com.sdgs.itb.service.data;

import com.sdgs.itb.infrastructure.data.dto.DataDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DataService {
    DataDTO createData(DataDTO dto);
    DataDTO updateData(Long id, DataDTO dto);
    void deleteData(Long id);
    DataDTO getData(Long id);
    Page<DataDTO> getData(
            String title,
            List<Long> goalIds,
            List<Long> categoryIds,
            List<Long> unitIds,
            String year,
            String sortBy,
            String sortDir,
            int page,
            int size
    );
}
