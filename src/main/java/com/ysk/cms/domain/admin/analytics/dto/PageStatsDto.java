package com.ysk.cms.domain.admin.analytics.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageStatsDto {

    private String pagePath;
    private String pageTitle;
    private Long pageViews;
    private Long uniquePageViews;
    private Integer avgTimeOnPage;
    private String avgTimeOnPageStr;
    private Long entrances;
    private Long exits;
    private BigDecimal bounceRate;
}
