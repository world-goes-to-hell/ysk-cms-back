package com.ysk.cms.domain.admin.analytics.repository;

import com.ysk.cms.domain.admin.analytics.entity.AnalyticsSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnalyticsSessionRepository extends JpaRepository<AnalyticsSession, Long> {

    Optional<AnalyticsSession> findBySessionId(String sessionId);

    // 기간별 세션 수
    @Query("SELECT COUNT(s) FROM AnalyticsSession s WHERE s.siteCode = :siteCode " +
           "AND s.startTime BETWEEN :startDate AND :endDate")
    Long countBySiteCodeAndDateRange(
            @Param("siteCode") String siteCode,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // 이탈 세션 수
    @Query("SELECT COUNT(s) FROM AnalyticsSession s WHERE s.siteCode = :siteCode " +
           "AND s.startTime BETWEEN :startDate AND :endDate AND s.isBounce = true")
    Long countBounceSessions(
            @Param("siteCode") String siteCode,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // 평균 세션 시간
    @Query("SELECT AVG(s.duration) FROM AnalyticsSession s WHERE s.siteCode = :siteCode " +
           "AND s.startTime BETWEEN :startDate AND :endDate AND s.duration > 0")
    Double findAvgSessionDuration(
            @Param("siteCode") String siteCode,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // 유입 경로별 세션 수
    @Query("SELECT " +
           "CASE " +
           "  WHEN s.referrer IS NULL OR s.referrer = '' THEN 'direct' " +
           "  WHEN s.referrer LIKE '%google%' OR s.referrer LIKE '%naver%' OR s.referrer LIKE '%bing%' OR s.referrer LIKE '%daum%' THEN 'search' " +
           "  WHEN s.referrer LIKE '%facebook%' OR s.referrer LIKE '%twitter%' OR s.referrer LIKE '%instagram%' OR s.referrer LIKE '%linkedin%' THEN 'social' " +
           "  ELSE 'referral' " +
           "END as type, " +
           "COUNT(s) as count " +
           "FROM AnalyticsSession s WHERE s.siteCode = :siteCode " +
           "AND s.startTime BETWEEN :startDate AND :endDate " +
           "GROUP BY type")
    List<Object[]> countByReferrerType(
            @Param("siteCode") String siteCode,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // 일별 세션 추이
    @Query("SELECT DATE(s.startTime) as date, COUNT(s) as sessions, " +
           "COUNT(DISTINCT s.visitorId) as visitors " +
           "FROM AnalyticsSession s WHERE s.siteCode = :siteCode " +
           "AND s.startTime BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(s.startTime) ORDER BY date")
    List<Object[]> findDailySessionTrend(
            @Param("siteCode") String siteCode,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
