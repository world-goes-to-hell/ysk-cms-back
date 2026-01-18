package com.ysk.cms.domain.admin.analytics.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrendDataDto {

    private List<String> labels;          // 날짜 레이블
    private List<Long> pageViews;         // 페이지뷰 데이터
    private List<Long> visitors;          // 방문자 데이터
    private List<Long> sessions;          // 세션 데이터

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailyData {
        private LocalDate date;
        private Long pageViews;
        private Long visitors;
        private Long sessions;
    }
}
