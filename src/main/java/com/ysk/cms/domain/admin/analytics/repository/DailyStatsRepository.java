package com.ysk.cms.domain.admin.analytics.repository;

import com.ysk.cms.domain.admin.analytics.entity.DailyStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyStatsRepository extends JpaRepository<DailyStats, Long> {

    Optional<DailyStats> findBySiteCodeAndStatsDate(String siteCode, LocalDate statsDate);

    List<DailyStats> findBySiteCodeAndStatsDateBetweenOrderByStatsDateAsc(
            String siteCode, LocalDate startDate, LocalDate endDate
    );

    // 기간 합계
    @Query("SELECT SUM(d.totalPageViews), SUM(d.uniqueVisitors), SUM(d.totalSessions), " +
           "AVG(d.avgSessionDuration), AVG(d.bounceRate) " +
           "FROM DailyStats d WHERE d.siteCode = :siteCode " +
           "AND d.statsDate BETWEEN :startDate AND :endDate")
    Object[] findSummaryBySiteCodeAndDateRange(
            @Param("siteCode") String siteCode,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
