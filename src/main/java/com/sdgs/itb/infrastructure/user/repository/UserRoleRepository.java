package com.sdgs.itb.infrastructure.user.repository;

import com.sdgs.itb.entity.user.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    void deleteByUserIdAndRoleId(Long userId, Long roleId);
}
