package com.ysk.cms.domain.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardStatsDto {

    private List<StatItem> stats;
    private PostStats postStats;

    @Getter
    @Builder
    public static class StatItem {
        private String id;
        private String title;
        private Long value;
        private String icon;
        private String trend;
        private Boolean trendUp;
    }

    @Getter
    @Builder
    public static class PostStats {
        private Long today;
        private Long thisWeek;
        private Long thisMonth;
    }
}
