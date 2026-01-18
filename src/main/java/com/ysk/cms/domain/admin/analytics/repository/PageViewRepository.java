package com.ysk.cms.domain.admin.analytics.repository;

import com.ysk.cms.domain.admin.analytics.entity.PageView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PageViewRepository extends JpaRepository<PageView, Long> {

    // 기간별 페이지뷰 수
    @Query("SELECT COUNT(p) FROM PageView p WHERE p.siteCode = :siteCode " +
           "AND p.createdAt BETWEEN :startDate AND :endDate")
    Long countBySiteCodeAndDateRange(
            @Param("siteCode") String siteCode,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // 기간별 순 방문자 수
    @Query("SELECT COUNT(DISTINCT p.visitorId) FROM PageView p WHERE p.siteCode = :siteCode " +
           "AND p.createdAt BETWEEN :startDate AND :endDate")
    Long countUniqueVisitorsBySiteCodeAndDateRange(
            @Param("siteCode") String siteCode,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // 시간대별 페이지뷰 수
    @Query("SELECT HOUR(p.createdAt) as hour, COUNT(p) as count FROM PageView p " +
           "WHERE p.siteCode = :siteCode AND p.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY HOUR(p.createdAt) ORDER BY hour")
    List<Object[]> countByHour(
            @Param("siteCode") String siteCode,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // 페이지별 조회수
    @Query("SELECT p.pagePath, p.pageTitle, COUNT(p) as views, COUNT(DISTINCT p.visitorId) as uniqueViews " +
           "FROM PageView p WHERE p.siteCode = :siteCode " +
           "AND p.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY p.pagePath, p.pageTitle ORDER BY views DESC")
    List<Object[]> findTopPagesBySiteCode(
            @Param("siteCode") String siteCode,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // 일별 페이지뷰 추이
    @Query("SELECT DATE(p.createdAt) as date, COUNT(p) as views, COUNT(DISTINCT p.visitorId) as visitors " +
           "FROM PageView p WHERE p.siteCode = :siteCode " +
           "AND p.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(p.createdAt) ORDER BY date")
    List<Object[]> findDailyTrend(
            @Param("siteCode") String siteCode,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // 디바이스별 통계
    @Query("SELECT p.deviceType, COUNT(p) as count FROM PageView p " +
           "WHERE p.siteCode = :siteCode AND p.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY p.deviceType")
    List<Object[]> countByDeviceType(
            @Param("siteCode") String siteCode,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // 브라우저별 통계
    @Query("SELECT p.browser, COUNT(p) as count FROM PageView p " +
           "WHERE p.siteCode = :siteCode AND p.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY p.browser ORDER BY count DESC")
    List<Object[]> countByBrowser(
            @Param("siteCode") String siteCode,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // 실시간 활성 사용자 (최근 5분)
    @Query("SELECT COUNT(DISTINCT p.visitorId) FROM PageView p " +
           "WHERE p.siteCode = :siteCode AND p.createdAt > :since")
    Long countActiveUsers(
            @Param("siteCode") String siteCode,
            @Param("since") LocalDateTime since
    );

    // 실시간 활성 페이지
    @Query("SELECT p.pagePath, COUNT(DISTINCT p.visitorId) as activeUsers FROM PageView p " +
           "WHERE p.siteCode = :siteCode AND p.createdAt > :since " +
           "GROUP BY p.pagePath ORDER BY activeUsers DESC")
    List<Object[]> findActivePages(
            @Param("siteCode") String siteCode,
            @Param("since") LocalDateTime since
    );
}
