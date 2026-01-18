package com.ysk.cms.domain.admin.activity.repository;

import com.ysk.cms.domain.admin.activity.entity.ActivityLog;
import com.ysk.cms.domain.admin.activity.entity.ActivityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    @Query("SELECT a FROM ActivityLog a ORDER BY a.createdAt DESC")
    Page<ActivityLog> findAllOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT a FROM ActivityLog a WHERE a.site.code = :siteCode ORDER BY a.createdAt DESC")
    Page<ActivityLog> findBySiteCodeOrderByCreatedAtDesc(@Param("siteCode") String siteCode, Pageable pageable);

    @Query("SELECT a FROM ActivityLog a WHERE a.user.id = :userId ORDER BY a.createdAt DESC")
    Page<ActivityLog> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT a FROM ActivityLog a LEFT JOIN FETCH a.user ORDER BY a.createdAt DESC")
    List<ActivityLog> findRecentActivities(Pageable pageable);

    @Query("SELECT a FROM ActivityLog a LEFT JOIN FETCH a.user WHERE a.site.code = :siteCode ORDER BY a.createdAt DESC")
    List<ActivityLog> findRecentActivitiesBySite(@Param("siteCode") String siteCode, Pageable pageable);

    @Query("SELECT COUNT(a) FROM ActivityLog a WHERE a.activityType = com.ysk.cms.domain.admin.activity.entity.ActivityType.LOGIN AND a.createdAt >= :startDate")
    long countLoginsSince(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT COUNT(a) FROM ActivityLog a WHERE a.activityType = com.ysk.cms.domain.admin.activity.entity.ActivityType.LOGIN AND a.createdAt >= :startDate AND a.createdAt < :endDate")
    long countLoginsBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM ActivityLog a LEFT JOIN FETCH a.user LEFT JOIN FETCH a.site " +
            "WHERE (:activityType IS NULL OR a.activityType = :activityType) " +
            "AND (:targetType IS NULL OR a.targetType = :targetType) " +
            "AND (:keyword IS NULL OR a.targetName LIKE %:keyword% OR a.description LIKE %:keyword%) " +
            "ORDER BY a.createdAt DESC")
    Page<ActivityLog> findAllWithFilters(
            @Param("activityType") ActivityType activityType,
            @Param("targetType") String targetType,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("SELECT a FROM ActivityLog a LEFT JOIN FETCH a.user LEFT JOIN FETCH a.site " +
            "WHERE a.site.code = :siteCode " +
            "AND (:activityType IS NULL OR a.activityType = :activityType) " +
            "AND (:targetType IS NULL OR a.targetType = :targetType) " +
            "AND (:keyword IS NULL OR a.targetName LIKE %:keyword% OR a.description LIKE %:keyword%) " +
            "ORDER BY a.createdAt DESC")
    Page<ActivityLog> findBySiteWithFilters(
            @Param("siteCode") String siteCode,
            @Param("activityType") ActivityType activityType,
            @Param("targetType") String targetType,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
