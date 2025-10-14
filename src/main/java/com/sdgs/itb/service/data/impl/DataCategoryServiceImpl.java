package com.sdgs.itb.service.data.impl;

import com.sdgs.itb.entity.data.DataCategory;
import com.sdgs.itb.entity.data.DataCategory;
import com.sdgs.itb.infrastructure.data.dto.DataCategoryDTO;
import com.sdgs.itb.infrastructure.data.mapper.DataCategoryMapper;
import com.sdgs.itb.infrastructure.data.dto.DataCategoryDTO;
import com.sdgs.itb.infrastructure.data.mapper.DataCategoryMapper;
import com.sdgs.itb.infrastructure.data.repository.DataCategoryRepository;
import com.sdgs.itb.service.data.DataCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DataCategoryServiceImpl implements DataCategoryService {

    private final DataCategoryRepository dataCategoryRepository;

    @Override
    public DataCategoryDTO createCategory(DataCategoryDTO dto) {
//        String userRole = Claims.getRoleFromJwt();
//        if (userRole == null ||
//                !(userRole.equals(RoleType.ADMIN_WEB) || userRole.equals(RoleType.ADMIN_FACULTY))) {
//            throw new UnauthorizedException("Only Admin can create new Data!");
//        }

        DataCategory dataCategory = DataCategoryMapper.toEntity(dto);

        return DataCategoryMapper.toDTO(dataCategoryRepository.save(dataCategory));
    }

    @Override
    public DataCategoryDTO updateCategory(Long id, DataCategoryDTO dto) {
//        String userRole = Claims.getRoleFromJwt();
//        if (userRole == null ||
//                !(userRole.equals(RoleType.ADMIN_WEB) || userRole.equals(RoleType.ADMIN_FACULTY))) {
//            throw new UnauthorizedException("Only Admin can create new Data!");
//        }

        DataCategory existing = dataCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Data not found"));

        existing.setCategory(dto.getCategory());

        return DataCategoryMapper.toDTO(dataCategoryRepository.save(existing));
    }

    @Override
    public void deleteCategory(Long id) {
//        String userRole = Claims.getRoleFromJwt();
//        if (userRole == null ||
//                !(userRole.equals(RoleType.ADMIN_WEB) || userRole.equals(RoleType.ADMIN_FACULTY))) {
//            throw new UnauthorizedException("Only Admin can create new Data!");
//        }
        DataCategory dataCategory = dataCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Data not found"));

        dataCategory.setDeletedAt(OffsetDateTime.now());

        dataCategoryRepository.save(dataCategory);
    }

    @Override
    public List<DataCategoryDTO> getCategories() {
        return dataCategoryRepository.findAll()
                .stream()
                .map(DataCategoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DataCategoryDTO getCategory(Long id) {
        DataCategory dataCategory = dataCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Data category not found"));
        return DataCategoryMapper.toDTO((dataCategory));
    }
}
