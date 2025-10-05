package com.sdgs.itb.infrastructure.unit.repository;

import com.sdgs.itb.entity.unit.UnitType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UnitTypeRepository extends JpaRepository<UnitType, Long> {

    @Query("SELECT u FROM Unit u WHERE LOWER(u.name) = LOWER(:name)")
    Optional<UnitType> findByName(String name);
}


