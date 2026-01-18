package com.ysk.cms.domain.admin.analytics.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RealtimeStatsDto {

    private Long activeUsers;
    private List<ActivePageDto> activePages;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ActivePageDto {
        private String pagePath;
        private Long activeUsers;
    }
}
