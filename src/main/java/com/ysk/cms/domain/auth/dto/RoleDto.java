package com.ysk.cms.domain.auth.dto;

import com.ysk.cms.domain.user.entity.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoleDto {

    private Long id;
    private String name;
    private String description;

    public static RoleDto from(Role role) {
        return RoleDto.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .build();
    }
}
