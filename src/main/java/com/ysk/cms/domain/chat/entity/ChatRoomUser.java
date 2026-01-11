package com.ysk.cms.domain.chat.entity;

import com.ysk.cms.common.entity.BaseEntity;
import com.ysk.cms.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_room_users", indexes = {
        @Index(name = "idx_chat_room_user_room", columnList = "chat_room_id"),
        @Index(name = "idx_chat_room_user_user", columnList = "user_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_chat_room_user", columnNames = {"chat_room_id", "user_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatRoomUser extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    private LocalDateTime joinedAt = LocalDateTime.now();

    private LocalDateTime lastReadAt;  // 마지막 읽은 시간 (읽지 않은 메시지 카운트용)

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;  // 채팅방 나가기 시 false

    public void updateLastReadAt() {
        this.lastReadAt = LocalDateTime.now();
    }

    public void leave() {
        this.isActive = false;
    }

    public void rejoin() {
        this.isActive = true;
        this.joinedAt = LocalDateTime.now();
    }
}
