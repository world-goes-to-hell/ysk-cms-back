package com.ysk.cms.domain.admin.chat.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ChatInviteRequest {

    @NotEmpty(message = "초대할 사용자를 선택해주세요")
    private List<Long> userIds;

    private String groupName;  // 1:1 → 그룹 전환 시 그룹 이름 (선택)
}
