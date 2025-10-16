package com.sdgs.itb.infrastructure.data.repository;

import com.sdgs.itb.entity.data.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DataRepository extends JpaRepository<Data, Long>, JpaSpecificationExecutor<Data> {

    @Query("SELECT a FROM Data a WHERE LOWER(a.sourceUrl) = LOWER(:url)")
    Optional<Data> findBySourceUrl(@Param("url") String url);

    @Query("SELECT a FROM Data a WHERE LOWER(a.title) = LOWER(:title)")
    Optional<Data> findByTitleIgnoreCase(@Param("title") String title);
}
