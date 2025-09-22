package com.sdgs.itb.infrastructure.user.repository;

import com.sdgs.itb.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query(value = "SELECT u FROM User u WHERE u.verificationToken = :verificationToken")
    Optional<User> findByVerificationToken(@Param("verificationToken") String verificationToken);

    boolean existsByEmail(String email);
}
