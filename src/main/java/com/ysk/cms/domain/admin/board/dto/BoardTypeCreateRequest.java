package com.ysk.cms.domain.admin.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BoardTypeCreateRequest {

    @NotBlank(message = "코드는 필수입니다")
    @Size(max = 50, message = "코드는 50자 이하여야 합니다")
    private String code;

    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 100, message = "이름은 100자 이하여야 합니다")
    private String name;

    @Size(max = 500, message = "설명은 500자 이하여야 합니다")
    private String description;

    @Size(max = 100, message = "아이콘은 100자 이하여야 합니다")
    private String icon = "mdi-file-document-outline";

    @Size(max = 20, message = "색상은 20자 이하여야 합니다")
    private String color = "#6366f1";

    @Size(max = 50, message = "배경색은 50자 이하여야 합니다")
    private String bgColor = "rgba(99, 102, 241, 0.1)";

    private Integer sortOrder = 0;
}
