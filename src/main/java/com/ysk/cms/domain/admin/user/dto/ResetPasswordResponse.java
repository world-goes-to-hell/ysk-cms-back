package com.ysk.cms.domain.admin.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResetPasswordResponse {
    private String temporaryPassword;
}
