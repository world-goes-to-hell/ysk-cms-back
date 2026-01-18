package com.ysk.cms.domain.admin.analytics.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "analytics_page_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "site_code", nullable = false, length = 50)
    private String siteCode;

    @Column(name = "stats_date", nullable = false)
    private LocalDate statsDate;

    @Column(name = "page_path", nullable = false, length = 500)
    private String pagePath;

    @Column(name = "page_title", length = 500)
    private String pageTitle;

    @Column(name = "page_views")
    @Builder.Default
    private Integer pageViews = 0;

    @Column(name = "unique_page_views")
    @Builder.Default
    private Integer uniquePageViews = 0;

    @Column(name = "avg_time_on_page")
    @Builder.Default
    private Integer avgTimeOnPage = 0;

    @Column(name = "entrances")
    @Builder.Default
    private Integer entrances = 0;

    @Column(name = "exits")
    @Builder.Default
    private Integer exits = 0;

    @Column(name = "bounce_rate", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal bounceRate = BigDecimal.ZERO;

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
