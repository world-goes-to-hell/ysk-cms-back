package com.ysk.cms.domain.admin.analytics.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "analytics_page_view")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "site_code", nullable = false, length = 50)
    private String siteCode;

    @Column(name = "session_id", nullable = false, length = 100)
    private String sessionId;

    @Column(name = "visitor_id", nullable = false, length = 100)
    private String visitorId;

    @Column(name = "user_id")
    private Long userId;

    // 페이지 정보
    @Column(name = "page_url", nullable = false, length = 2000)
    private String pageUrl;

    @Column(name = "page_path", nullable = false, length = 500)
    private String pagePath;

    @Column(name = "page_title", length = 500)
    private String pageTitle;

    @Column(name = "referrer", length = 2000)
    private String referrer;

    // 디바이스 정보
    @Column(name = "user_agent", length = 1000)
    private String userAgent;

    @Column(name = "device_type", length = 20)
    private String deviceType;

    @Column(name = "browser", length = 50)
    private String browser;

    @Column(name = "browser_version", length = 50)
    private String browserVersion;

    @Column(name = "os", length = 50)
    private String os;

    @Column(name = "os_version", length = 50)
    private String osVersion;

    // 화면 정보
    @Column(name = "screen_width")
    private Integer screenWidth;

    @Column(name = "screen_height")
    private Integer screenHeight;

    // 지역 정보
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "city", length = 100)
    private String city;

    // UTM 파라미터
    @Column(name = "utm_source", length = 200)
    private String utmSource;

    @Column(name = "utm_medium", length = 200)
    private String utmMedium;

    @Column(name = "utm_campaign", length = 200)
    private String utmCampaign;

    @Column(name = "utm_term", length = 200)
    private String utmTerm;

    @Column(name = "utm_content", length = 200)
    private String utmContent;

    // 시간 정보
    @Column(name = "time_on_page")
    private Integer timeOnPage;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
