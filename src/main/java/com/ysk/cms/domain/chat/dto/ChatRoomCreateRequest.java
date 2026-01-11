package com.ysk.cms.domain.chat.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomCreateRequest {

    @Size(max = 100, message = "채팅방 이름은 100자를 초과할 수 없습니다")
    private String name;  // 그룹 채팅방 이름 (선택)

    @NotEmpty(message = "참여자는 최소 1명 이상이어야 합니다")
    private List<Long> participantIds;  // 참여할 사용자 ID 목록
}
