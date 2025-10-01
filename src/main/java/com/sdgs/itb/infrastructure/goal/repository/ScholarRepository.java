package com.sdgs.itb.infrastructure.goal.repository;

import com.sdgs.itb.entity.goal.Scholar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ScholarRepository extends JpaRepository<Scholar, Long> {

    @Query("SELECT s FROM Scholar s WHERE LOWER(s.name) = LOWER(:name)")
    Optional<Scholar> findByName(@Param("name") String name);
}
