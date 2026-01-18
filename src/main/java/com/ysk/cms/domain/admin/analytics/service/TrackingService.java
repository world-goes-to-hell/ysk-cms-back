package com.ysk.cms.domain.admin.analytics.service;

import com.ysk.cms.domain.admin.analytics.dto.TrackingEventDto;
import com.ysk.cms.domain.admin.analytics.entity.AnalyticsSession;
import com.ysk.cms.domain.admin.analytics.entity.PageView;
import com.ysk.cms.domain.admin.analytics.entity.Visitor;
import com.ysk.cms.domain.admin.analytics.repository.AnalyticsSessionRepository;
import com.ysk.cms.domain.admin.analytics.repository.PageViewRepository;
import com.ysk.cms.domain.admin.analytics.repository.VisitorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackingService {

    private final PageViewRepository pageViewRepository;
    private final AnalyticsSessionRepository sessionRepository;
    private final VisitorRepository visitorRepository;
    private final UserAgentParser userAgentParser;

    @Transactional
    public void track(String siteCode, TrackingEventDto event, String userAgent, String ipAddress) {
        log.debug("[Analytics] Track event: {} for site: {}", event.getEventType(), siteCode);

        if ("pageview".equals(event.getEventType())) {
            trackPageView(siteCode, event, userAgent, ipAddress);
        } else if ("pageleave".equals(event.getEventType())) {
            trackPageLeave(siteCode, event);
        }
    }

    private void trackPageView(String siteCode, TrackingEventDto event, String userAgent, String ipAddress) {
        // User-Agent 파싱
        UserAgentParser.ParsedUserAgent parsed = userAgentParser.parse(userAgent);

        // 페이지뷰 저장
        PageView pageView = PageView.builder()
                .siteCode(siteCode)
                .sessionId(event.getSessionId())
                .visitorId(event.getVisitorId())
                .userId(event.getUserId())
                .pageUrl(event.getPageUrl())
                .pagePath(event.getPagePath())
                .pageTitle(event.getPageTitle())
                .referrer(event.getReferrer())
                .userAgent(userAgent)
                .deviceType(parsed.getDeviceType())
                .browser(parsed.getBrowser())
                .browserVersion(parsed.getBrowserVersion())
                .os(parsed.getOs())
                .osVersion(parsed.getOsVersion())
                .screenWidth(event.getScreenWidth())
                .screenHeight(event.getScreenHeight())
                .ipAddress(maskIpAddress(ipAddress))
                .utmSource(event.getUtmSource())
                .utmMedium(event.getUtmMedium())
                .utmCampaign(event.getUtmCampaign())
                .utmTerm(event.getUtmTerm())
                .utmContent(event.getUtmContent())
                .build();
        pageViewRepository.save(pageView);

        // 세션 업데이트 또는 생성
        updateOrCreateSession(siteCode, event, parsed, ipAddress);

        // 방문자 업데이트 또는 생성
        updateOrCreateVisitor(siteCode, event);
    }

    private void trackPageLeave(String siteCode, TrackingEventDto event) {
        // 체류 시간 업데이트 (최근 페이지뷰 찾아서 업데이트)
        // 간단 구현: 세션 종료 시간만 업데이트
        sessionRepository.findBySessionId(event.getSessionId())
                .ifPresent(session -> {
                    session.setEndTime(LocalDateTime.now());
                    if (event.getTimeOnPage() != null && event.getTimeOnPage() > 0) {
                        session.setDuration(session.getDuration() + event.getTimeOnPage());
                    }
                    session.setExitPage(event.getPagePath());
                });
    }

    private void updateOrCreateSession(String siteCode, TrackingEventDto event,
                                       UserAgentParser.ParsedUserAgent parsed, String ipAddress) {
        sessionRepository.findBySessionId(event.getSessionId())
                .ifPresentOrElse(
                        session -> {
                            // 기존 세션 업데이트
                            session.updatePageView(event.getPagePath());
                        },
                        () -> {
                            // 새 세션 생성
                            AnalyticsSession session = AnalyticsSession.builder()
                                    .sessionId(event.getSessionId())
                                    .siteCode(siteCode)
                                    .visitorId(event.getVisitorId())
                                    .userId(event.getUserId())
                                    .startTime(LocalDateTime.now())
                                    .entryPage(event.getPagePath())
                                    .exitPage(event.getPagePath())
                                    .referrer(event.getReferrer())
                                    .deviceType(parsed.getDeviceType())
                                    .browser(parsed.getBrowser())
                                    .os(parsed.getOs())
                                    .utmSource(event.getUtmSource())
                                    .utmMedium(event.getUtmMedium())
                                    .utmCampaign(event.getUtmCampaign())
                                    .build();
                            sessionRepository.save(session);
                        }
                );
    }

    private void updateOrCreateVisitor(String siteCode, TrackingEventDto event) {
        visitorRepository.findBySiteCodeAndVisitorId(siteCode, event.getVisitorId())
                .ifPresentOrElse(
                        Visitor::incrementPageView,
                        () -> {
                            // 새 방문자 생성
                            Visitor visitor = Visitor.builder()
                                    .visitorId(event.getVisitorId())
                                    .siteCode(siteCode)
                                    .firstVisitAt(LocalDateTime.now())
                                    .lastVisitAt(LocalDateTime.now())
                                    .firstReferrer(event.getReferrer())
                                    .firstUtmSource(event.getUtmSource())
                                    .firstUtmMedium(event.getUtmMedium())
                                    .firstUtmCampaign(event.getUtmCampaign())
                                    .build();
                            visitorRepository.save(visitor);
                        }
                );
    }

    /**
     * IP 주소 익명화 (마지막 옥텟 제거)
     */
    private String maskIpAddress(String ipAddress) {
        if (ipAddress == null) return null;
        int lastDot = ipAddress.lastIndexOf('.');
        if (lastDot > 0) {
            return ipAddress.substring(0, lastDot) + ".0";
        }
        return ipAddress;
    }
}
