package com.ysk.cms.domain.admin.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TypingNotification {

    private Long roomId;
    private Long userId;
    private String username;
    private String nickname;
    private boolean typing;
}
