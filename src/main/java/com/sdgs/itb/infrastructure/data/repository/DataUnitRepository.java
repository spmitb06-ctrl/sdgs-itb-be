package com.sdgs.itb.infrastructure.data.repository;

import com.sdgs.itb.entity.data.DataUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DataUnitRepository extends JpaRepository<DataUnit, Long> {

    @Query("""
        SELECT du FROM DataUnit du
        WHERE du.data.id = :dataId
          AND du.deletedAt IS NULL
    """)
    List<DataUnit> findAllByDataId(@Param("dataId") Long dataId);

    @Query("""
        SELECT du
        FROM DataUnit du
        WHERE du.data.id = :dataId
          AND du.unit.id = :unitId
          AND du.deletedAt IS NULL
    """)
    Optional<DataUnit> findByDataIdAndUnitId(
            @Param("dataId") Long dataId,
            @Param("unitId") Long unitId
    );
}
