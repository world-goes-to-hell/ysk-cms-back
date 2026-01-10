package com.ysk.cms.domain.activity.repository;

import com.ysk.cms.domain.activity.entity.ActivityLog;
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

    @Query("SELECT COUNT(a) FROM ActivityLog a WHERE a.activityType = com.ysk.cms.domain.activity.entity.ActivityType.LOGIN AND a.createdAt >= :startDate")
    long countLoginsSince(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT COUNT(a) FROM ActivityLog a WHERE a.activityType = com.ysk.cms.domain.activity.entity.ActivityType.LOGIN AND a.createdAt >= :startDate AND a.createdAt < :endDate")
    long countLoginsBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
