package com.ysk.cms.domain.admin.site.dto;

import com.ysk.cms.domain.admin.site.entity.Site;
import com.ysk.cms.domain.admin.site.entity.SiteStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SiteDto {

    private Long id;
    private String code;
    private String name;
    private String description;
    private String domain;
    private SiteStatus status;
    private String settings;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;

    public static SiteDto from(Site site) {
        return SiteDto.builder()
                .id(site.getId())
                .code(site.getCode())
                .name(site.getName())
                .description(site.getDescription())
                .domain(site.getDomain())
                .status(site.getStatus())
                .settings(site.getSettings())
                .createdAt(site.getCreatedAt())
                .updatedAt(site.getUpdatedAt())
                .createdBy(site.getCreatedBy())
                .build();
    }
}
