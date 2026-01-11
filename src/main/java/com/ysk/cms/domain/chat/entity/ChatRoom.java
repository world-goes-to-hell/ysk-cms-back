package com.ysk.cms.domain.chat.entity;

import com.ysk.cms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "chat_rooms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatRoom extends BaseEntity {

    @Column(length = 100)
    private String name;  // 그룹 채팅방 이름 (1:1은 null)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ChatRoomType type = ChatRoomType.PRIVATE;

    @Column(columnDefinition = "TEXT")
    private String lastMessage;

    private LocalDateTime lastMessageAt;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ChatRoomUser> participants = new HashSet<>();

    public void updateLastMessage(String content) {
        this.lastMessage = content;
        this.lastMessageAt = LocalDateTime.now();
    }

    public void updateName(String name) {
        this.name = name;
    }
}
