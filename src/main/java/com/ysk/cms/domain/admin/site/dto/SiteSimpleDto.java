package com.ysk.cms.domain.admin.site.dto;

import com.ysk.cms.domain.admin.site.entity.Site;
import lombok.Builder;
import lombok.Getter;

/**
 * 사이트 간단 정보 DTO (로그인 선택용)
 */
@Getter
@Builder
public class SiteSimpleDto {
    private Long id;
    private String code;
    private String name;

    public static SiteSimpleDto from(Site site) {
        return SiteSimpleDto.builder()
                .id(site.getId())
                .code(site.getCode())
                .name(site.getName())
                .build();
    }
}
