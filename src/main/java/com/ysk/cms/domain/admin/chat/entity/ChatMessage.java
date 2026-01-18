package com.ysk.cms.domain.admin.chat.entity;

import com.ysk.cms.common.entity.BaseEntity;
import com.ysk.cms.domain.admin.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_messages", indexes = {
        @Index(name = "idx_chat_message_room", columnList = "chat_room_id"),
        @Index(name = "idx_chat_message_created", columnList = "chat_room_id, created_at DESC")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatMessage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MessageType type = MessageType.TEXT;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    // 파일 첨부시
    @Column(length = 255)
    private String fileName;

    @Column(length = 500)
    private String fileUrl;

    private Long fileSize;
}
