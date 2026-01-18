package com.ysk.cms.domain.admin.analytics.controller;

import com.ysk.cms.common.dto.ApiResponse;
import com.ysk.cms.domain.admin.analytics.dto.*;
import com.ysk.cms.domain.admin.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 통계 조회 API (관리자용)
 */
@RestController
@RequestMapping("/api/sites/{siteCode}/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    /**
     * 요약 통계 조회
     * GET /api/sites/{siteCode}/analytics/summary?startDate=2026-01-01&endDate=2026-01-18
     */
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<AnalyticsSummaryDto>> getSummary(
            @PathVariable String siteCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        AnalyticsSummaryDto summary = analyticsService.getSummary(siteCode, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    /**
     * 일별 추이 데이터
     * GET /api/sites/{siteCode}/analytics/trend
     */
    @GetMapping("/trend")
    public ResponseEntity<ApiResponse<TrendDataDto>> getTrend(
            @PathVariable String siteCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        TrendDataDto trend = analyticsService.getTrend(siteCode, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(trend));
    }

    /**
     * 인기 페이지 목록
     * GET /api/sites/{siteCode}/analytics/pages?limit=10
     */
    @GetMapping("/pages")
    public ResponseEntity<ApiResponse<List<PageStatsDto>>> getTopPages(
            @PathVariable String siteCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<PageStatsDto> pages = analyticsService.getTopPages(siteCode, startDate, endDate, limit);
        return ResponseEntity.ok(ApiResponse.success(pages));
    }

    /**
     * 디바이스 통계
     * GET /api/sites/{siteCode}/analytics/devices
     */
    @GetMapping("/devices")
    public ResponseEntity<ApiResponse<DeviceStatsDto>> getDeviceStats(
            @PathVariable String siteCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        DeviceStatsDto stats = analyticsService.getDeviceStats(siteCode, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    /**
     * 유입 경로 통계
     * GET /api/sites/{siteCode}/analytics/referrers
     */
    @GetMapping("/referrers")
    public ResponseEntity<ApiResponse<ReferrerStatsDto>> getReferrerStats(
            @PathVariable String siteCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        ReferrerStatsDto stats = analyticsService.getReferrerStats(siteCode, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    /**
     * 실시간 통계
     * GET /api/sites/{siteCode}/analytics/realtime
     */
    @GetMapping("/realtime")
    public ResponseEntity<ApiResponse<RealtimeStatsDto>> getRealtimeStats(
            @PathVariable String siteCode
    ) {
        RealtimeStatsDto stats = analyticsService.getRealtimeStats(siteCode);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
