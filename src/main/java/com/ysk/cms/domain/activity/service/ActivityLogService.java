package com.ysk.cms.domain.activity.service;

import com.ysk.cms.domain.activity.entity.ActivityLog;
import com.ysk.cms.domain.activity.entity.ActivityType;
import com.ysk.cms.domain.activity.repository.ActivityLogRepository;
import com.ysk.cms.domain.site.entity.Site;
import com.ysk.cms.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    @Async
    @Transactional
    public void log(User user, Site site, ActivityType activityType,
                    String targetType, Long targetId, String targetName, String ipAddress) {
        ActivityLog log = ActivityLog.builder()
                .user(user)
                .site(site)
                .activityType(activityType)
                .targetType(targetType)
                .targetId(targetId)
                .targetName(targetName)
                .ipAddress(ipAddress)
                .build();

        activityLogRepository.save(log);
    }

    @Transactional
    public void logSync(User user, Site site, ActivityType activityType,
                        String targetType, Long targetId, String targetName, String ipAddress) {
        ActivityLog log = ActivityLog.builder()
                .user(user)
                .site(site)
                .activityType(activityType)
                .targetType(targetType)
                .targetId(targetId)
                .targetName(targetName)
                .ipAddress(ipAddress)
                .build();

        activityLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<ActivityLog> getRecentActivities(int limit) {
        return activityLogRepository.findRecentActivities(PageRequest.of(0, limit));
    }

    @Transactional(readOnly = true)
    public List<ActivityLog> getRecentActivitiesBySite(String siteCode, int limit) {
        return activityLogRepository.findRecentActivitiesBySite(siteCode, PageRequest.of(0, limit));
    }
}
