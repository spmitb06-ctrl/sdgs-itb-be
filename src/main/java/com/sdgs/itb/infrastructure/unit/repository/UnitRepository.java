package com.sdgs.itb.infrastructure.unit.repository;

import com.sdgs.itb.entity.unit.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UnitRepository extends JpaRepository<Unit, Long>, JpaSpecificationExecutor<Unit> {

    @Query("SELECT u FROM Unit u WHERE LOWER(u.name) = LOWER(:name)")
    Optional<Unit> findByName(String name);

    @Query("SELECT u FROM Unit u WHERE LOWER(u.abbreviation) = LOWER(:abbreviation)")
    Optional<Unit> findByAbbreviation(String abbreviation);

    @Query("SELECT u FROM Unit u WHERE u.organizationId = :organizationId AND u.deletedAt IS NULL")
    List<Unit> findByOrganizationId(@Param("organizationId") Long organizationId);
}


