package com.ysk.cms.domain.page.dto;

import com.ysk.cms.domain.page.entity.PageStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PageUpdateRequest {

    @NotBlank(message = "슬러그는 필수입니다")
    @Size(max = 100, message = "슬러그는 100자 이하여야 합니다")
    private String slug;

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 200, message = "제목은 200자 이하여야 합니다")
    private String title;

    private String content;

    @Size(max = 300, message = "메타 설명은 300자 이하여야 합니다")
    private String metaDescription;

    @Size(max = 500, message = "메타 키워드는 500자 이하여야 합니다")
    private String metaKeywords;

    private PageStatus status;

    private Long parentId;

    private Integer sortOrder;
}
