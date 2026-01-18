package com.ysk.cms.domain.admin.analytics.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "analytics_session")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false, unique = true, length = 100)
    private String sessionId;

    @Column(name = "site_code", nullable = false, length = 50)
    private String siteCode;

    @Column(name = "visitor_id", nullable = false, length = 100)
    private String visitorId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "page_views")
    @Builder.Default
    private Integer pageViews = 1;

    @Column(name = "duration")
    @Builder.Default
    private Integer duration = 0;

    @Column(name = "is_bounce")
    @Builder.Default
    private Boolean isBounce = true;

    @Column(name = "entry_page", length = 500)
    private String entryPage;

    @Column(name = "exit_page", length = 500)
    private String exitPage;

    @Column(name = "referrer", length = 2000)
    private String referrer;

    @Column(name = "device_type", length = 20)
    private String deviceType;

    @Column(name = "browser", length = 50)
    private String browser;

    @Column(name = "os", length = 50)
    private String os;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "utm_source", length = 200)
    private String utmSource;

    @Column(name = "utm_medium", length = 200)
    private String utmMedium;

    @Column(name = "utm_campaign", length = 200)
    private String utmCampaign;

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

    public void updatePageView(String pagePath) {
        this.pageViews++;
        this.exitPage = pagePath;
        this.isBounce = false;
        this.endTime = LocalDateTime.now();
        this.duration = (int) java.time.Duration.between(startTime, endTime).getSeconds();
    }
}
