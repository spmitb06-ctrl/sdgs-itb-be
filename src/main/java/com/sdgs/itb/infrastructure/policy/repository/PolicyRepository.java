package com.sdgs.itb.infrastructure.policy.repository;

import com.sdgs.itb.entity.policy.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PolicyRepository extends JpaRepository<Policy, Long>, JpaSpecificationExecutor<Policy> {

    @Query("SELECT a FROM Policy a WHERE LOWER(a.sourceUrl) = LOWER(:url)")
    Optional<Policy> findBySourceUrl(@Param("url") String url);

    @Query("SELECT a FROM Policy a WHERE LOWER(a.title) = LOWER(:title)")
    Optional<Policy> findByTitleIgnoreCase(@Param("title") String title);
}
