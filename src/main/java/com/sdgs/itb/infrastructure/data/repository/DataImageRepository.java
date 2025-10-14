package com.sdgs.itb.infrastructure.data.repository;

import com.sdgs.itb.entity.data.DataImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DataImageRepository extends JpaRepository<DataImage, Long> {

    @Query("""
        SELECT di FROM DataImage di
        WHERE di.data.id = :dataId
          AND di.deletedAt IS NULL
    """)
    List<DataImage> findAllByDataId(@Param("dataId") Long dataId);

    @Query("""
        SELECT di FROM DataImage di
        WHERE di.imageUrl = :imageUrl
          AND di.deletedAt IS NULL
    """)
    Optional<DataImage> findByImageUrl(@Param("imageUrl") String imageUrl);
}


