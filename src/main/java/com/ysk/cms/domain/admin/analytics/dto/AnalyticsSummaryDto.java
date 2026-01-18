package com.ysk.cms.domain.admin.analytics.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsSummaryDto {

    // 방문자 지표
    private Long totalPageViews;
    private Long uniqueVisitors;
    private Long totalSessions;
    private Long newVisitors;
    private Long returningVisitors;

    // 세션 지표
    private Integer avgSessionDuration;     // 초
    private String avgSessionDurationStr;   // "3:45" 형식
    private BigDecimal bounceRate;          // %
    private BigDecimal avgPageViews;        // 페이지/세션

    // 증감률 (전 기간 대비)
    private BigDecimal pageViewsChange;
    private BigDecimal visitorsChange;
    private BigDecimal sessionsChange;
    private BigDecimal bounceRateChange;

    // 실시간
    private Long activeUsers;
}
