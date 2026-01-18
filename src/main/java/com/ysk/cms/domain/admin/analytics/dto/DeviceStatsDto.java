package com.ysk.cms.domain.admin.analytics.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceStatsDto {

    private List<DeviceData> devices;
    private List<BrowserData> browsers;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DeviceData {
        private String deviceType;
        private Long count;
        private BigDecimal percentage;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BrowserData {
        private String browser;
        private Long count;
        private BigDecimal percentage;
    }
}
