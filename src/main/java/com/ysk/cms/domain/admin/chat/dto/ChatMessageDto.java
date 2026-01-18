package com.ysk.cms.domain.admin.chat.dto;

import com.ysk.cms.domain.admin.chat.entity.ChatMessage;
import com.ysk.cms.domain.admin.chat.entity.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDto {

    private Long id;
    private Long roomId;
    private Long senderId;
    private String senderUsername;
    private String senderNickname;
    private MessageType type;
    private String content;
    private String fileName;
    private String fileUrl;
    private Long fileSize;
    private LocalDateTime createdAt;

    public static ChatMessageDto from(ChatMessage message) {
        return ChatMessageDto.builder()
                .id(message.getId())
                .roomId(message.getChatRoom().getId())
                .senderId(message.getSender().getId())
                .senderUsername(message.getSender().getUsername())
                .senderNickname(message.getSender().getName())
                .type(message.getType())
                .content(message.getContent())
                .fileName(message.getFileName())
                .fileUrl(message.getFileUrl())
                .fileSize(message.getFileSize())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
