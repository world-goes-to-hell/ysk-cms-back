package com.ysk.cms.domain.board.dto;

import com.ysk.cms.domain.board.entity.BoardTypeStatus;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BoardTypeUpdateRequest {

    @Size(max = 100, message = "이름은 100자 이하여야 합니다")
    private String name;

    @Size(max = 500, message = "설명은 500자 이하여야 합니다")
    private String description;

    @Size(max = 100, message = "아이콘은 100자 이하여야 합니다")
    private String icon;

    @Size(max = 20, message = "색상은 20자 이하여야 합니다")
    private String color;

    @Size(max = 50, message = "배경색은 50자 이하여야 합니다")
    private String bgColor;

    private Integer sortOrder;

    private BoardTypeStatus status;
}
