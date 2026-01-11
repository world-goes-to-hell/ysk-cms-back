package com.ysk.cms.domain.board.dto;

import com.ysk.cms.domain.board.entity.BoardStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BoardUpdateRequest {

    @NotBlank(message = "게시판명은 필수입니다")
    @Size(max = 100, message = "게시판명은 100자 이하여야 합니다")
    private String name;

    @Size(max = 500, message = "설명은 500자 이하여야 합니다")
    private String description;

    @Size(max = 50, message = "게시판 타입 코드는 50자 이하여야 합니다")
    private String typeCode;

    private Boolean useComment;

    private Boolean useAttachment;

    private Integer attachmentLimit;

    private Boolean useSecret;

    private Boolean usePinned;

    private Integer sortOrder;

    private BoardStatus status;
}
