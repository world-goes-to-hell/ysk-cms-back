package com.ysk.cms.domain.chat.dto;

import com.ysk.cms.domain.chat.entity.ChatRoomUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomUserDto {

    private Long id;
    private Long userId;
    private String username;
    private String nickname;
    private LocalDateTime joinedAt;
    private LocalDateTime lastReadAt;
    private Boolean isActive;

    public static ChatRoomUserDto from(ChatRoomUser chatRoomUser) {
        return ChatRoomUserDto.builder()
                .id(chatRoomUser.getId())
                .userId(chatRoomUser.getUser().getId())
                .username(chatRoomUser.getUser().getUsername())
                .nickname(chatRoomUser.getUser().getName())
                .joinedAt(chatRoomUser.getJoinedAt())
                .lastReadAt(chatRoomUser.getLastReadAt())
                .isActive(chatRoomUser.getIsActive())
                .build();
    }
}
