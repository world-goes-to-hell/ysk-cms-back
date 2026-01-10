package com.ysk.cms.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenRefreshResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
}
