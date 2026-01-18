package com.ysk.cms.domain.admin.auth.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private UserInfo user;
    private SiteInfo site;

    @Getter
    @Builder
    public static class UserInfo {
        private Long id;
        private String username;
        private String name;
        private String email;
        private List<String> roles;
    }

    @Getter
    @Builder
    public static class SiteInfo {
        private Long id;
        private String code;
        private String name;
    }
}
