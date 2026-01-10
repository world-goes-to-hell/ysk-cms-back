package com.ysk.cms.domain.site.dto;

import com.ysk.cms.domain.site.entity.SiteStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SiteUpdateRequest {

    @NotBlank(message = "사이트 이름을 입력해주세요.")
    @Size(max = 100, message = "사이트 이름은 100자 이하여야 합니다.")
    private String name;

    @Size(max = 500, message = "설명은 500자 이하여야 합니다.")
    private String description;

    @Size(max = 255, message = "도메인은 255자 이하여야 합니다.")
    private String domain;

    private SiteStatus status;

    private String settings;
}
