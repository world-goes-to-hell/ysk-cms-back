package com.ysk.cms.domain.admin.atchfile.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AtchFileUpdateRequest {

    @Size(max = 500, message = "설명은 500자 이하여야 합니다")
    private String description;

    @Size(max = 500, message = "대체 텍스트는 500자 이하여야 합니다")
    private String altText;
}
