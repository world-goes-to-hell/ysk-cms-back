package com.ysk.cms.domain.admin.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecentActivityDto {

    private Long id;
    private String user;
    private String action;
    private String time;
    private String icon;
    private String targetType;
    private String targetName;
}
