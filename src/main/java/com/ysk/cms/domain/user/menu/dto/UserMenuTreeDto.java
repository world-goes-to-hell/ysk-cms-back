package com.ysk.cms.domain.user.menu.dto;

import com.ysk.cms.domain.admin.role.dto.RoleDto;
import com.ysk.cms.domain.user.menu.entity.UserMenu;
import com.ysk.cms.domain.user.menu.entity.UserMenuStatus;
import com.ysk.cms.domain.user.menu.entity.UserMenuType;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class UserMenuTreeDto {

    private Long id;
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
    private List<RoleDto> roles;
    private List<UserMenuTreeDto> children;

    public static UserMenuTreeDto from(UserMenu menu) {
        return UserMenuTreeDto.builder()
                .id(menu.getId())
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
                .roles(menu.getRoles() != null ?
                        menu.getRoles().stream()
                                .map(RoleDto::from)
                                .collect(Collectors.toList())
                        : List.of())
                .children(new ArrayList<>())
                .build();
    }

    public static UserMenuTreeDto fromWithChildren(UserMenu menu) {
        UserMenuTreeDto dto = from(menu);
        if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
            dto.children = menu.getChildren().stream()
                    .filter(child -> !child.getDeleted())
                    .map(UserMenuTreeDto::fromWithChildren)
                    .toList();
        }
        return dto;
    }
}
