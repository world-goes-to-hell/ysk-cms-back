package com.ysk.cms.domain.admin.user.dto;

import com.ysk.cms.domain.admin.user.entity.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateUserStatusRequest {

    @NotNull(message = "상태는 필수입니다")
    private UserStatus status;
}
