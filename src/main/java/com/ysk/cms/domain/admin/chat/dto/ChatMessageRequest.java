package com.ysk.cms.domain.admin.chat.dto;

import com.ysk.cms.domain.admin.chat.entity.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest {

    @NotNull(message = "메시지 타입은 필수입니다")
    private MessageType type;

    @NotBlank(message = "메시지 내용은 필수입니다")
    private String content;

    // 파일 첨부 시
    private String fileName;
    private String fileUrl;
    private Long fileSize;
}
