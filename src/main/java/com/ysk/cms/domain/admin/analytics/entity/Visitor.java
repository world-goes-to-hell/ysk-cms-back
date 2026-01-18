package com.ysk.cms.domain.admin.analytics.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "analytics_visitor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Visitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "visitor_id", nullable = false, length = 100)
    private String visitorId;

    @Column(name = "site_code", nullable = false, length = 50)
    private String siteCode;

    @Column(name = "first_visit_at", nullable = false)
    private LocalDateTime firstVisitAt;

    @Column(name = "last_visit_at", nullable = false)
    private LocalDateTime lastVisitAt;

    @Column(name = "total_visits")
    @Builder.Default
    private Integer totalVisits = 1;

    @Column(name = "total_page_views")
    @Builder.Default
    private Integer totalPageViews = 1;

    @Column(name = "first_referrer", length = 2000)
    private String firstReferrer;

    @Column(name = "first_utm_source", length = 200)
    private String firstUtmSource;

    @Column(name = "first_utm_medium", length = 200)
    private String firstUtmMedium;

    @Column(name = "first_utm_campaign", length = 200)
    private String firstUtmCampaign;

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

    public void incrementVisit() {
        this.totalVisits++;
        this.lastVisitAt = LocalDateTime.now();
    }

    public void incrementPageView() {
        this.totalPageViews++;
        this.lastVisitAt = LocalDateTime.now();
    }
}
