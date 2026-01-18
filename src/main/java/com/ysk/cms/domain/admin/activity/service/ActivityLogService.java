package com.ysk.cms.domain.admin.activity.service;

import com.ysk.cms.common.dto.PageResponse;
import com.ysk.cms.domain.admin.activity.dto.ActivityLogDto;
import com.ysk.cms.domain.admin.activity.entity.ActivityLog;
import com.ysk.cms.domain.admin.activity.entity.ActivityType;
import com.ysk.cms.domain.admin.activity.repository.ActivityLogRepository;
import com.ysk.cms.domain.admin.site.entity.Site;
import com.ysk.cms.domain.admin.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Transactional(readOnly = true)
    public PageResponse<ActivityLogDto> getAllActivityLogs(ActivityType activityType, String targetType, String keyword, Pageable pageable) {
        Page<ActivityLog> page = activityLogRepository.findAllWithFilters(activityType, targetType, keyword, pageable);
        return PageResponse.of(page.map(ActivityLogDto::from));
    }

    @Transactional(readOnly = true)
    public PageResponse<ActivityLogDto> getActivityLogsBySite(String siteCode, ActivityType activityType, String targetType, String keyword, Pageable pageable) {
        Page<ActivityLog> page = activityLogRepository.findBySiteWithFilters(siteCode, activityType, targetType, keyword, pageable);
        return PageResponse.of(page.map(ActivityLogDto::from));
    }

    @Transactional(readOnly = true)
    public PageResponse<ActivityLogDto> getActivityLogsByUser(Long userId, Pageable pageable) {
        Page<ActivityLog> page = activityLogRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return PageResponse.of(page.map(ActivityLogDto::from));
    }
}
