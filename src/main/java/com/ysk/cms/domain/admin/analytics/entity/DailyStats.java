package com.ysk.cms.domain.admin.analytics.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "analytics_daily_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "site_code", nullable = false, length = 50)
    private String siteCode;

    @Column(name = "stats_date", nullable = false)
    private LocalDate statsDate;

    // 방문자 지표
    @Column(name = "total_visitors")
    @Builder.Default
    private Integer totalVisitors = 0;

    @Column(name = "unique_visitors")
    @Builder.Default
    private Integer uniqueVisitors = 0;

    @Column(name = "new_visitors")
    @Builder.Default
    private Integer newVisitors = 0;

    @Column(name = "returning_visitors")
    @Builder.Default
    private Integer returningVisitors = 0;

    // 페이지 지표
    @Column(name = "total_page_views")
    @Builder.Default
    private Integer totalPageViews = 0;

    @Column(name = "avg_page_views", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal avgPageViews = BigDecimal.ZERO;

    // 세션 지표
    @Column(name = "total_sessions")
    @Builder.Default
    private Integer totalSessions = 0;

    @Column(name = "avg_session_duration")
    @Builder.Default
    private Integer avgSessionDuration = 0;

    @Column(name = "bounce_sessions")
    @Builder.Default
    private Integer bounceSessions = 0;

    @Column(name = "bounce_rate", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal bounceRate = BigDecimal.ZERO;

    // JSON 필드
    @Column(name = "hourly_page_views", columnDefinition = "JSON")
    private String hourlyPageViews;

    @Column(name = "hourly_sessions", columnDefinition = "JSON")
    private String hourlySessions;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
