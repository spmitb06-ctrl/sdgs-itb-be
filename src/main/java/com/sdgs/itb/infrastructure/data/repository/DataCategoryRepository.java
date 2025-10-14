package com.sdgs.itb.infrastructure.data.repository;

import com.sdgs.itb.entity.data.DataCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataCategoryRepository extends JpaRepository<DataCategory, Long> {
}


