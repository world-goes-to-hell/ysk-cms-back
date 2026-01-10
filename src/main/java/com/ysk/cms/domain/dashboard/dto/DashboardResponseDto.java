package com.ysk.cms.domain.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardResponseDto {

    private DashboardStatsDto.StatItem users;
    private DashboardStatsDto.StatItem contents;
    private DashboardStatsDto.StatItem visits;
    private DashboardStatsDto.StatItem signups;
    private DashboardStatsDto.ArticleStats articleStats;
    private List<RecentArticleDto> recentArticles;
    private List<RecentActivityDto> recentActivities;
}
