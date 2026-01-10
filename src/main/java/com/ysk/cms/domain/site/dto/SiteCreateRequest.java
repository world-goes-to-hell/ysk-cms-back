package com.ysk.cms.domain.site.dto;

import com.ysk.cms.domain.site.entity.Site;
import com.ysk.cms.domain.site.entity.SiteStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SiteCreateRequest {

    @NotBlank(message = "사이트 코드를 입력해주세요.")
    @Size(min = 2, max = 50, message = "사이트 코드는 2~50자 사이여야 합니다.")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "사이트 코드는 영문 소문자, 숫자, 하이픈만 사용 가능합니다.")
    private String code;

    @NotBlank(message = "사이트 이름을 입력해주세요.")
    @Size(max = 100, message = "사이트 이름은 100자 이하여야 합니다.")
    private String name;

    @Size(max = 500, message = "설명은 500자 이하여야 합니다.")
    private String description;

    @Size(max = 255, message = "도메인은 255자 이하여야 합니다.")
    private String domain;

    private SiteStatus status = SiteStatus.ACTIVE;

    private String settings;

    public Site toEntity() {
        return Site.builder()
                .code(code)
                .name(name)
                .description(description)
                .domain(domain)
                .status(status != null ? status : SiteStatus.ACTIVE)
                .settings(settings)
                .build();
    }
}
