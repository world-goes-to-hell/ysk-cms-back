package com.ysk.cms.domain.admin.analytics.service;

import com.ysk.cms.domain.admin.analytics.dto.*;
import com.ysk.cms.domain.admin.analytics.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AnalyticsService {

    private final PageViewRepository pageViewRepository;
    private final AnalyticsSessionRepository sessionRepository;
    private final VisitorRepository visitorRepository;
    private final DailyStatsRepository dailyStatsRepository;

    /**
     * 요약 통계 조회
     */
    public AnalyticsSummaryDto getSummary(String siteCode, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        // 현재 기간 통계
        Long pageViews = pageViewRepository.countBySiteCodeAndDateRange(siteCode, startDateTime, endDateTime);
        Long uniqueVisitors = pageViewRepository.countUniqueVisitorsBySiteCodeAndDateRange(siteCode, startDateTime, endDateTime);
        Long sessions = sessionRepository.countBySiteCodeAndDateRange(siteCode, startDateTime, endDateTime);
        Long bounceSessions = sessionRepository.countBounceSessions(siteCode, startDateTime, endDateTime);
        Double avgDuration = sessionRepository.findAvgSessionDuration(siteCode, startDateTime, endDateTime);

        Long newVisitors = visitorRepository.countNewVisitors(siteCode, startDateTime, endDateTime);
        Long returningVisitors = visitorRepository.countReturningVisitors(siteCode, startDateTime, endDateTime);

        // 이탈률 계산
        BigDecimal bounceRate = sessions > 0
                ? BigDecimal.valueOf(bounceSessions * 100.0 / sessions).setScale(1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // 평균 페이지뷰/세션
        BigDecimal avgPageViews = sessions > 0
                ? BigDecimal.valueOf(pageViews * 1.0 / sessions).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // 평균 세션 시간 포맷
        int avgDurationSec = avgDuration != null ? avgDuration.intValue() : 0;
        String avgDurationStr = formatDuration(avgDurationSec);

        // 이전 기간 대비 증감률 계산
        long daysDiff = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        LocalDate prevStartDate = startDate.minusDays(daysDiff);
        LocalDate prevEndDate = startDate.minusDays(1);
        LocalDateTime prevStartDateTime = prevStartDate.atStartOfDay();
        LocalDateTime prevEndDateTime = prevEndDate.atTime(LocalTime.MAX);

        Long prevPageViews = pageViewRepository.countBySiteCodeAndDateRange(siteCode, prevStartDateTime, prevEndDateTime);
        Long prevVisitors = pageViewRepository.countUniqueVisitorsBySiteCodeAndDateRange(siteCode, prevStartDateTime, prevEndDateTime);
        Long prevSessions = sessionRepository.countBySiteCodeAndDateRange(siteCode, prevStartDateTime, prevEndDateTime);

        // 실시간 활성 사용자 (최근 5분)
        Long activeUsers = pageViewRepository.countActiveUsers(siteCode, LocalDateTime.now().minusMinutes(5));

        return AnalyticsSummaryDto.builder()
                .totalPageViews(pageViews)
                .uniqueVisitors(uniqueVisitors)
                .totalSessions(sessions)
                .newVisitors(newVisitors)
                .returningVisitors(returningVisitors)
                .avgSessionDuration(avgDurationSec)
                .avgSessionDurationStr(avgDurationStr)
                .bounceRate(bounceRate)
                .avgPageViews(avgPageViews)
                .pageViewsChange(calculateChangeRate(pageViews, prevPageViews))
                .visitorsChange(calculateChangeRate(uniqueVisitors, prevVisitors))
                .sessionsChange(calculateChangeRate(sessions, prevSessions))
                .activeUsers(activeUsers)
                .build();
    }

    /**
     * 일별 추이 데이터
     */
    public TrendDataDto getTrend(String siteCode, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Object[]> dailyData = pageViewRepository.findDailyTrend(siteCode, startDateTime, endDateTime);

        List<String> labels = new ArrayList<>();
        List<Long> pageViews = new ArrayList<>();
        List<Long> visitors = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");

        // 날짜 범위의 모든 날짜에 대해 데이터 생성 (없으면 0)
        Map<LocalDate, Object[]> dataMap = dailyData.stream()
                .collect(Collectors.toMap(
                        row -> ((java.sql.Date) row[0]).toLocalDate(),
                        row -> row
                ));

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            labels.add(date.format(formatter));
            Object[] row = dataMap.get(date);
            if (row != null) {
                pageViews.add(((Number) row[1]).longValue());
                visitors.add(((Number) row[2]).longValue());
            } else {
                pageViews.add(0L);
                visitors.add(0L);
            }
        }

        return TrendDataDto.builder()
                .labels(labels)
                .pageViews(pageViews)
                .visitors(visitors)
                .build();
    }

    /**
     * 인기 페이지 목록
     */
    public List<PageStatsDto> getTopPages(String siteCode, LocalDate startDate, LocalDate endDate, int limit) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Object[]> results = pageViewRepository.findTopPagesBySiteCode(siteCode, startDateTime, endDateTime);

        return results.stream()
                .limit(limit)
                .map(row -> PageStatsDto.builder()
                        .pagePath((String) row[0])
                        .pageTitle((String) row[1])
                        .pageViews(((Number) row[2]).longValue())
                        .uniquePageViews(((Number) row[3]).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 디바이스 통계
     */
    public DeviceStatsDto getDeviceStats(String siteCode, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Object[]> deviceData = pageViewRepository.countByDeviceType(siteCode, startDateTime, endDateTime);
        List<Object[]> browserData = pageViewRepository.countByBrowser(siteCode, startDateTime, endDateTime);

        long totalDevices = deviceData.stream().mapToLong(row -> ((Number) row[1]).longValue()).sum();
        long totalBrowsers = browserData.stream().mapToLong(row -> ((Number) row[1]).longValue()).sum();

        List<DeviceStatsDto.DeviceData> devices = deviceData.stream()
                .map(row -> {
                    String type = (String) row[0];
                    long count = ((Number) row[1]).longValue();
                    BigDecimal percentage = totalDevices > 0
                            ? BigDecimal.valueOf(count * 100.0 / totalDevices).setScale(1, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;
                    return DeviceStatsDto.DeviceData.builder()
                            .deviceType(type != null ? type : "unknown")
                            .count(count)
                            .percentage(percentage)
                            .build();
                })
                .collect(Collectors.toList());

        List<DeviceStatsDto.BrowserData> browsers = browserData.stream()
                .limit(5)
                .map(row -> {
                    String browser = (String) row[0];
                    long count = ((Number) row[1]).longValue();
                    BigDecimal percentage = totalBrowsers > 0
                            ? BigDecimal.valueOf(count * 100.0 / totalBrowsers).setScale(1, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;
                    return DeviceStatsDto.BrowserData.builder()
                            .browser(browser != null ? browser : "unknown")
                            .count(count)
                            .percentage(percentage)
                            .build();
                })
                .collect(Collectors.toList());

        return DeviceStatsDto.builder()
                .devices(devices)
                .browsers(browsers)
                .build();
    }

    /**
     * 유입 경로 통계
     */
    public ReferrerStatsDto getReferrerStats(String siteCode, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Object[]> results = sessionRepository.countByReferrerType(siteCode, startDateTime, endDateTime);

        long total = results.stream().mapToLong(row -> ((Number) row[1]).longValue()).sum();

        Map<String, String> typeNames = Map.of(
                "direct", "직접 접속",
                "search", "검색 엔진",
                "social", "소셜 미디어",
                "referral", "참조 사이트"
        );

        List<ReferrerStatsDto.ReferrerData> referrers = results.stream()
                .map(row -> {
                    String type = (String) row[0];
                    long count = ((Number) row[1]).longValue();
                    BigDecimal percentage = total > 0
                            ? BigDecimal.valueOf(count * 100.0 / total).setScale(1, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;
                    return ReferrerStatsDto.ReferrerData.builder()
                            .type(type)
                            .typeName(typeNames.getOrDefault(type, type))
                            .count(count)
                            .percentage(percentage)
                            .build();
                })
                .collect(Collectors.toList());

        return ReferrerStatsDto.builder()
                .referrers(referrers)
                .build();
    }

    /**
     * 실시간 통계
     */
    public RealtimeStatsDto getRealtimeStats(String siteCode) {
        LocalDateTime since = LocalDateTime.now().minusMinutes(5);

        Long activeUsers = pageViewRepository.countActiveUsers(siteCode, since);
        List<Object[]> activePages = pageViewRepository.findActivePages(siteCode, since);

        List<RealtimeStatsDto.ActivePageDto> pages = activePages.stream()
                .limit(10)
                .map(row -> RealtimeStatsDto.ActivePageDto.builder()
                        .pagePath((String) row[0])
                        .activeUsers(((Number) row[1]).longValue())
                        .build())
                .collect(Collectors.toList());

        return RealtimeStatsDto.builder()
                .activeUsers(activeUsers)
                .activePages(pages)
                .build();
    }

    // === Helper Methods ===

    private String formatDuration(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%d:%02d", minutes, secs);
    }

    private BigDecimal calculateChangeRate(Long current, Long previous) {
        if (previous == null || previous == 0) {
            return current > 0 ? BigDecimal.valueOf(100) : BigDecimal.ZERO;
        }
        return BigDecimal.valueOf((current - previous) * 100.0 / previous)
                .setScale(1, RoundingMode.HALF_UP);
    }
}
