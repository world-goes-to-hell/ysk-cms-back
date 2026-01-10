package com.ysk.cms.domain.dashboard.service;

import com.ysk.cms.domain.activity.entity.ActivityLog;
import com.ysk.cms.domain.activity.repository.ActivityLogRepository;
import com.ysk.cms.domain.dashboard.dto.*;
import com.ysk.cms.domain.post.entity.Post;
import com.ysk.cms.domain.post.repository.PostRepository;
import com.ysk.cms.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ActivityLogRepository activityLogRepository;

    public DashboardResponseDto getDashboardData() {
        return DashboardResponseDto.builder()
                .users(getUserStats())
                .contents(getContentStats())
                .visits(getVisitStats())
                .signups(getSignupStats())
                .postStats(getPostStats())
                .recentPosts(getRecentPosts(5))
                .recentActivities(getRecentActivities(4))
                .build();
    }

    public DashboardResponseDto getDashboardDataBySite(String siteCode) {
        // 사이트별 대시보드 - 추후 사이트 필터링 구현
        return getDashboardData();
    }

    private DashboardStatsDto.StatItem getUserStats() {
        long totalUsers = userRepository.countActiveUsers();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastWeekStart = now.minusWeeks(1).with(LocalTime.MIN);
        LocalDateTime twoWeeksAgoStart = now.minusWeeks(2).with(LocalTime.MIN);

        long thisWeekNewUsers = userRepository.countUsersCreatedSince(lastWeekStart);
        long lastWeekNewUsers = userRepository.countUsersCreatedBetween(twoWeeksAgoStart, lastWeekStart);

        String trend = calculateTrend(thisWeekNewUsers, lastWeekNewUsers);
        boolean trendUp = thisWeekNewUsers >= lastWeekNewUsers;

        return DashboardStatsDto.StatItem.builder()
                .id("users")
                .title("총 사용자")
                .value(totalUsers)
                .icon("User")
                .trend(trend)
                .trendUp(trendUp)
                .build();
    }

    private DashboardStatsDto.StatItem getContentStats() {
        long totalContents = postRepository.countAllActive();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastWeekStart = now.minusWeeks(1).with(LocalTime.MIN);
        LocalDateTime twoWeeksAgoStart = now.minusWeeks(2).with(LocalTime.MIN);

        long thisWeekPosts = postRepository.countPostsCreatedSince(lastWeekStart);
        long lastWeekPosts = postRepository.countPostsCreatedBetween(twoWeeksAgoStart, lastWeekStart);

        String trend = calculateTrend(thisWeekPosts, lastWeekPosts);
        boolean trendUp = thisWeekPosts >= lastWeekPosts;

        return DashboardStatsDto.StatItem.builder()
                .id("contents")
                .title("총 콘텐츠")
                .value(totalContents)
                .icon("Document")
                .trend(trend)
                .trendUp(trendUp)
                .build();
    }

    private DashboardStatsDto.StatItem getVisitStats() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime yesterdayStart = todayStart.minusDays(1);

        long todayVisits = activityLogRepository.countLoginsSince(todayStart);
        long yesterdayVisits = activityLogRepository.countLoginsBetween(yesterdayStart, todayStart);

        String trend = calculateTrend(todayVisits, yesterdayVisits);
        boolean trendUp = todayVisits >= yesterdayVisits;

        return DashboardStatsDto.StatItem.builder()
                .id("visits")
                .title("오늘 방문")
                .value(todayVisits)
                .icon("View")
                .trend(trend)
                .trendUp(trendUp)
                .build();
    }

    private DashboardStatsDto.StatItem getSignupStats() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime yesterdayStart = todayStart.minusDays(1);

        long todaySignups = userRepository.countUsersCreatedSince(todayStart);
        long yesterdaySignups = userRepository.countUsersCreatedBetween(yesterdayStart, todayStart);

        String trend = calculateTrend(todaySignups, yesterdaySignups);
        boolean trendUp = todaySignups >= yesterdaySignups;

        return DashboardStatsDto.StatItem.builder()
                .id("signups")
                .title("신규 가입")
                .value(todaySignups)
                .icon("Plus")
                .trend(trend)
                .trendUp(trendUp)
                .build();
    }

    private DashboardStatsDto.PostStats getPostStats() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime weekStart = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1).atStartOfDay();
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        long today = postRepository.countPostsCreatedSince(todayStart);
        long thisWeek = postRepository.countPostsCreatedSince(weekStart);
        long thisMonth = postRepository.countPostsCreatedSince(monthStart);

        return DashboardStatsDto.PostStats.builder()
                .today(today)
                .thisWeek(thisWeek)
                .thisMonth(thisMonth)
                .build();
    }

    private List<RecentPostDto> getRecentPosts(int limit) {
        List<Post> posts = postRepository.findRecentPosts(PageRequest.of(0, limit));
        return posts.stream()
                .map(RecentPostDto::from)
                .toList();
    }

    private List<RecentActivityDto> getRecentActivities(int limit) {
        List<ActivityLog> activities = activityLogRepository.findRecentActivities(PageRequest.of(0, limit));
        return activities.stream()
                .map(this::toRecentActivityDto)
                .toList();
    }

    private RecentActivityDto toRecentActivityDto(ActivityLog log) {
        return RecentActivityDto.builder()
                .id(log.getId())
                .user(log.getUser() != null ? log.getUser().getName() : "시스템")
                .action(log.getActionText())
                .time(formatTimeAgo(log.getCreatedAt()))
                .icon(log.getIcon())
                .targetType(log.getTargetType())
                .targetName(log.getTargetName())
                .build();
    }

    private String calculateTrend(long current, long previous) {
        if (previous == 0) {
            if (current > 0) {
                return "+100%";
            }
            return "0%";
        }

        long diff = current - previous;
        long percent = (diff * 100) / previous;

        if (percent >= 0) {
            return "+" + percent + "%";
        }
        return percent + "%";
    }

    private String formatTimeAgo(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }

        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);

        if (minutes < 1) {
            return "방금 전";
        } else if (minutes < 60) {
            return minutes + "분 전";
        } else if (minutes < 1440) { // 24 * 60
            return (minutes / 60) + "시간 전";
        } else {
            return (minutes / 1440) + "일 전";
        }
    }
}
