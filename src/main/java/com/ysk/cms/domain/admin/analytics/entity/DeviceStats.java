package com.ysk.cms.domain.admin.analytics.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "analytics_device_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "site_code", nullable = false, length = 50)
    private String siteCode;

    @Column(name = "stats_date", nullable = false)
    private LocalDate statsDate;

    @Column(name = "device_type", nullable = false, length = 20)
    private String deviceType;  // desktop, mobile, tablet

    @Column(name = "browser", length = 50)
    private String browser;

    @Column(name = "os", length = 50)
    private String os;

    @Column(name = "sessions")
    @Builder.Default
    private Integer sessions = 0;

    @Column(name = "unique_visitors")
    @Builder.Default
    private Integer uniqueVisitors = 0;

    @Column(name = "page_views")
    @Builder.Default
    private Integer pageViews = 0;

    @Column(name = "bounce_rate", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal bounceRate = BigDecimal.ZERO;

    @Column(name = "avg_session_duration")
    @Builder.Default
    private Integer avgSessionDuration = 0;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
