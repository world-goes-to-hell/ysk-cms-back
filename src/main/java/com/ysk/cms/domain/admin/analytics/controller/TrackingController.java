package com.ysk.cms.domain.admin.analytics.controller;

import com.ysk.cms.domain.admin.analytics.dto.TrackingEventDto;
import com.ysk.cms.domain.admin.analytics.service.TrackingService;
import com.ysk.cms.security.annotation.SkipMenuAuth;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 트래킹 이벤트 수집 API (공개 API)
 */
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Slf4j
@SkipMenuAuth
public class TrackingController {

    private final TrackingService trackingService;

    /**
     * 트래킹 이벤트 수집
     * POST /api/analytics/track/{siteCode}
     */
    @PostMapping("/track/{siteCode}")
    public ResponseEntity<Void> track(
            @PathVariable String siteCode,
            @RequestBody TrackingEventDto event,
            HttpServletRequest request
    ) {
        String userAgent = request.getHeader("User-Agent");
        String ipAddress = getClientIpAddress(request);

        trackingService.track(siteCode, event, userAgent, ipAddress);

        return ResponseEntity.ok().build();
    }

    /**
     * Beacon API 용 (navigator.sendBeacon)
     * Content-Type: text/plain 으로 전송되는 경우
     */
    @PostMapping(value = "/track/{siteCode}", consumes = "text/plain")
    public ResponseEntity<Void> trackBeacon(
            @PathVariable String siteCode,
            @RequestBody String body,
            HttpServletRequest request
    ) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            TrackingEventDto event = mapper.readValue(body, TrackingEventDto.class);

            String userAgent = request.getHeader("User-Agent");
            String ipAddress = getClientIpAddress(request);

            trackingService.track(siteCode, event, userAgent, ipAddress);
        } catch (Exception e) {
            log.warn("[Analytics] Failed to parse beacon data: {}", e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    /**
     * 클라이언트 IP 주소 추출
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP"
        };

        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // X-Forwarded-For는 쉼표로 구분된 IP 목록일 수 있음
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }
}
