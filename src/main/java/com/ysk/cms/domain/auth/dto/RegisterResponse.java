package com.ysk.cms.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterResponse {

    private Long id;
    private String username;
    private String name;
    private String email;
    private String status;
    private String message;
}
