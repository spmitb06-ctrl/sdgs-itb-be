package com.sdgs.itb.infrastructure.policy.repository;

import com.sdgs.itb.entity.policy.PolicyCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PolicyCategoryRepository extends JpaRepository<PolicyCategory, Long> {
}


