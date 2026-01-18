package com.ysk.cms.domain.admin.analytics.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReferrerStatsDto {

    private List<ReferrerData> referrers;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReferrerData {
        private String type;          // direct, search, social, referral
        private String typeName;      // 직접 접속, 검색 엔진, 소셜, 참조 사이트
        private Long count;
        private BigDecimal percentage;
    }
}
