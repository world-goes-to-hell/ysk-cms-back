package com.ysk.cms.domain.admin.dashboard.service;

import com.ysk.cms.domain.admin.activity.entity.ActivityLog;
import com.ysk.cms.domain.admin.activity.repository.ActivityLogRepository;
import com.ysk.cms.domain.admin.dashboard.dto.DashboardResponseDto;
import com.ysk.cms.domain.admin.dashboard.dto.DashboardStatsDto;
import com.ysk.cms.domain.admin.dashboard.dto.RecentActivityDto;
import com.ysk.cms.domain.admin.dashboard.dto.RecentArticleDto;
import com.ysk.cms.domain.admin.dashboard.dto.*;
import com.ysk.cms.domain.admin.article.entity.BoardArticle;
import com.ysk.cms.domain.admin.article.repository.BoardArticleRepository;
import com.ysk.cms.domain.admin.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final BoardArticleRepository articleRepository;
    private final ActivityLogRepository activityLogRepository;

    public DashboardResponseDto getDashboardData() {
        return DashboardResponseDto.builder()
                .users(getUserStats())
                .contents(getContentStats())
                .visits(getVisitStats())
                .signups(getSignupStats())
                .articleStats(getArticleStats())
                .recentArticles(getRecentArticles(5))
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
        long totalContents = articleRepository.countAllActive();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastWeekStart = now.minusWeeks(1).with(LocalTime.MIN);
        LocalDateTime twoWeeksAgoStart = now.minusWeeks(2).with(LocalTime.MIN);

        long thisWeekArticles = articleRepository.countArticlesCreatedSince(lastWeekStart);
        long lastWeekArticles = articleRepository.countArticlesCreatedBetween(twoWeeksAgoStart, lastWeekStart);

        String trend = calculateTrend(thisWeekArticles, lastWeekArticles);
        boolean trendUp = thisWeekArticles >= lastWeekArticles;

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

    private DashboardStatsDto.ArticleStats getArticleStats() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime weekStart = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1).atStartOfDay();
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        long today = articleRepository.countArticlesCreatedSince(todayStart);
        long thisWeek = articleRepository.countArticlesCreatedSince(weekStart);
        long thisMonth = articleRepository.countArticlesCreatedSince(monthStart);

        return DashboardStatsDto.ArticleStats.builder()
                .today(today)
                .thisWeek(thisWeek)
                .thisMonth(thisMonth)
                .build();
    }

    private List<RecentArticleDto> getRecentArticles(int limit) {
        List<BoardArticle> articles = articleRepository.findRecentArticles(PageRequest.of(0, limit));
        return articles.stream()
                .map(RecentArticleDto::from)
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
