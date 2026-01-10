package com.ysk.cms.domain.media.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MediaUploadRequest {

    @Size(max = 500, message = "설명은 500자 이하여야 합니다")
    private String description;

    @Size(max = 500, message = "대체 텍스트는 500자 이하여야 합니다")
    private String altText;
}
