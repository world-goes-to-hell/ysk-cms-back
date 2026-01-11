package com.ysk.cms.domain.chat.dto;

import com.ysk.cms.domain.chat.entity.ChatRoom;
import com.ysk.cms.domain.chat.entity.ChatRoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomDto {

    private Long id;
    private String name;
    private ChatRoomType type;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private List<ChatRoomUserDto> participants;
    private int unreadCount;
    private LocalDateTime createdAt;

    public static ChatRoomDto from(ChatRoom chatRoom, List<ChatRoomUserDto> participants, int unreadCount) {
        return ChatRoomDto.builder()
                .id(chatRoom.getId())
                .name(chatRoom.getName())
                .type(chatRoom.getType())
                .lastMessage(chatRoom.getLastMessage())
                .lastMessageAt(chatRoom.getLastMessageAt())
                .participants(participants)
                .unreadCount(unreadCount)
                .createdAt(chatRoom.getCreatedAt())
                .build();
    }
}
