package com.ysk.cms.domain.activity.dto;

import com.ysk.cms.domain.activity.entity.ActivityLog;
import com.ysk.cms.domain.activity.entity.ActivityType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Getter
@Builder
public class ActivityLogDto {

    private Long id;
    private String userName;
    private String userEmail;
    private String siteCode;
    private String siteName;
    private ActivityType activityType;
    private String targetType;
    private Long targetId;
    private String targetName;
    private String description;
    private String ipAddress;
    private String icon;
    private String action;
    private String timeAgo;
    private LocalDateTime createdAt;

    public static ActivityLogDto from(ActivityLog log) {
        return ActivityLogDto.builder()
                .id(log.getId())
                .userName(log.getUser() != null ? log.getUser().getName() : "시스템")
                .userEmail(log.getUser() != null ? log.getUser().getEmail() : null)
                .siteCode(log.getSite() != null ? log.getSite().getCode() : null)
                .siteName(log.getSite() != null ? log.getSite().getName() : "전체")
                .activityType(log.getActivityType())
                .targetType(log.getTargetType())
                .targetId(log.getTargetId())
                .targetName(log.getTargetName())
                .description(log.getDescription())
                .ipAddress(log.getIpAddress())
                .icon(log.getIcon())
                .action(log.getActionText())
                .timeAgo(getTimeAgo(log.getCreatedAt()))
                .createdAt(log.getCreatedAt())
                .build();
    }

    private static String getTimeAgo(LocalDateTime dateTime) {
        if (dateTime == null) return "";

        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);

        if (minutes < 1) return "방금 전";
        if (minutes < 60) return minutes + "분 전";

        long hours = ChronoUnit.HOURS.between(dateTime, now);
        if (hours < 24) return hours + "시간 전";

        long days = ChronoUnit.DAYS.between(dateTime, now);
        if (days < 7) return days + "일 전";

        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
