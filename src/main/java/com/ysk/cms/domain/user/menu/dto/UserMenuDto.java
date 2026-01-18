package com.ysk.cms.domain.user.menu.dto;

import com.ysk.cms.domain.admin.role.dto.RoleDto;
import com.ysk.cms.domain.user.menu.entity.UserMenu;
import com.ysk.cms.domain.user.menu.entity.UserMenuStatus;
import com.ysk.cms.domain.user.menu.entity.UserMenuType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class UserMenuDto {

    private Long id;
    private Long parentId;
    private String parentName;
    private String name;
    private String code;
    private UserMenuType type;
    private String url;
    private String icon;
    private Integer sortOrder;
    private UserMenuStatus status;
    private String target;
    private String description;
    private Integer depth;
    private Boolean hasChildren;
    private List<RoleDto> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserMenuDto from(UserMenu menu) {
        return UserMenuDto.builder()
                .id(menu.getId())
                .parentId(menu.getParent() != null ? menu.getParent().getId() : null)
                .parentName(menu.getParent() != null ? menu.getParent().getName() : null)
                .name(menu.getName())
                .code(menu.getCode())
                .type(menu.getType())
                .url(menu.getUrl())
                .icon(menu.getIcon())
                .sortOrder(menu.getSortOrder())
                .status(menu.getStatus())
                .target(menu.getTarget())
                .description(menu.getDescription())
                .depth(menu.getDepth())
                .hasChildren(menu.hasChildren())
                .roles(menu.getRoles() != null ?
                        menu.getRoles().stream()
                                .map(RoleDto::from)
                                .collect(Collectors.toList())
                        : List.of())
                .createdAt(menu.getCreatedAt())
                .updatedAt(menu.getUpdatedAt())
                .build();
    }
}
