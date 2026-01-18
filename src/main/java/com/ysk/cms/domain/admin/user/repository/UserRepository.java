package com.ysk.cms.domain.admin.user.repository;

import com.ysk.cms.domain.admin.user.entity.User;
import com.ysk.cms.domain.admin.user.entity.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.deleted = false")
    Page<User> findAllActive(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.deleted = false AND u.status = :status")
    Page<User> findAllByStatus(@Param("status") UserStatus status, Pageable pageable);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username AND u.deleted = false")
    Optional<User> findByUsernameWithRoles(@Param("username") String username);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id AND u.deleted = false")
    Optional<User> findByIdWithRoles(@Param("id") Long id);

    @Query("SELECT COUNT(u) FROM User u WHERE u.deleted = false")
    long countActiveUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.deleted = false AND u.createdAt >= :startDate")
    long countUsersCreatedSince(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT COUNT(u) FROM User u WHERE u.deleted = false AND u.createdAt >= :startDate AND u.createdAt < :endDate")
    long countUsersCreatedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN u.roles r WHERE u.deleted = false " +
            "AND (:roleName IS NULL OR r.name = :roleName) " +
            "AND (:status IS NULL OR u.status = :status) " +
            "AND (:keyword IS NULL OR u.name LIKE %:keyword% OR u.email LIKE %:keyword% OR u.username LIKE %:keyword%)")
    Page<User> findAllWithFilters(
            @Param("roleName") String roleName,
            @Param("status") UserStatus status,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
