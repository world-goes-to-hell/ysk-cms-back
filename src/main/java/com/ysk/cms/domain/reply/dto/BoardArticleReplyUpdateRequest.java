package com.ysk.cms.domain.reply.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardArticleReplyUpdateRequest {

    @NotBlank(message = "댓글 내용은 필수입니다")
    @Size(max = 2000, message = "댓글은 2000자 이내로 작성해주세요")
    private String content;

    private Boolean isSecret;
}
