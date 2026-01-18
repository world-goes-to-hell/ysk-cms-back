package com.ysk.cms.domain.admin.analytics.service;

import lombok.*;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User-Agent 문자열 파싱 (외부 라이브러리 없이 간단 구현)
 */
@Component
public class UserAgentParser {

    public ParsedUserAgent parse(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return ParsedUserAgent.builder()
                    .deviceType("unknown")
                    .browser("unknown")
                    .browserVersion("")
                    .os("unknown")
                    .osVersion("")
                    .build();
        }

        String ua = userAgent.toLowerCase();

        return ParsedUserAgent.builder()
                .deviceType(parseDeviceType(ua))
                .browser(parseBrowser(userAgent))
                .browserVersion(parseBrowserVersion(userAgent))
                .os(parseOS(userAgent))
                .osVersion(parseOSVersion(userAgent))
                .build();
    }

    private String parseDeviceType(String ua) {
        if (ua.contains("mobile") || ua.contains("android") && !ua.contains("tablet")) {
            return "mobile";
        } else if (ua.contains("tablet") || ua.contains("ipad")) {
            return "tablet";
        }
        return "desktop";
    }

    private String parseBrowser(String userAgent) {
        if (userAgent.contains("Edg/")) return "Edge";
        if (userAgent.contains("OPR/") || userAgent.contains("Opera")) return "Opera";
        if (userAgent.contains("Chrome/") && !userAgent.contains("Edg/")) return "Chrome";
        if (userAgent.contains("Safari/") && !userAgent.contains("Chrome")) return "Safari";
        if (userAgent.contains("Firefox/")) return "Firefox";
        if (userAgent.contains("MSIE") || userAgent.contains("Trident/")) return "IE";
        return "Other";
    }

    private String parseBrowserVersion(String userAgent) {
        Pattern[] patterns = {
                Pattern.compile("Edg/([\\d.]+)"),
                Pattern.compile("OPR/([\\d.]+)"),
                Pattern.compile("Chrome/([\\d.]+)"),
                Pattern.compile("Version/([\\d.]+).*Safari"),
                Pattern.compile("Firefox/([\\d.]+)"),
                Pattern.compile("MSIE ([\\d.]+)"),
                Pattern.compile("rv:([\\d.]+)")
        };

        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(userAgent);
            if (matcher.find()) {
                String version = matcher.group(1);
                // 메이저 버전만 반환
                int dotIndex = version.indexOf('.');
                return dotIndex > 0 ? version.substring(0, dotIndex) : version;
            }
        }
        return "";
    }

    private String parseOS(String userAgent) {
        if (userAgent.contains("Windows")) return "Windows";
        if (userAgent.contains("Mac OS X") || userAgent.contains("Macintosh")) return "macOS";
        if (userAgent.contains("iPhone") || userAgent.contains("iPad")) return "iOS";
        if (userAgent.contains("Android")) return "Android";
        if (userAgent.contains("Linux")) return "Linux";
        return "Other";
    }

    private String parseOSVersion(String userAgent) {
        Pattern[] patterns = {
                Pattern.compile("Windows NT ([\\d.]+)"),
                Pattern.compile("Mac OS X ([\\d_]+)"),
                Pattern.compile("iPhone OS ([\\d_]+)"),
                Pattern.compile("Android ([\\d.]+)"),
        };

        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(userAgent);
            if (matcher.find()) {
                return matcher.group(1).replace("_", ".");
            }
        }
        return "";
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ParsedUserAgent {
        private String deviceType;
        private String browser;
        private String browserVersion;
        private String os;
        private String osVersion;
    }
}
