package com.ysk.cms.domain.admin.analytics.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackingEventDto {

    private String eventType;       // pageview, pageleave, event
    private String visitorId;
    private String sessionId;
    private Long userId;

    // 페이지 정보
    private String pageUrl;
    private String pagePath;
    private String pageTitle;
    private String referrer;

    // 화면 정보
    private Integer screenWidth;
    private Integer screenHeight;
    private String language;

    // UTM 파라미터
    private String utmSource;
    private String utmMedium;
    private String utmCampaign;
    private String utmTerm;
    private String utmContent;

    // 체류 시간 (pageleave 시)
    private Integer timeOnPage;

    // 타임스탬프
    private String timestamp;
}
