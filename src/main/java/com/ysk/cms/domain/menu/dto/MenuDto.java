package com.ysk.cms.domain.menu.dto;

import com.ysk.cms.domain.menu.entity.Menu;
import com.ysk.cms.domain.menu.entity.MenuStatus;
import com.ysk.cms.domain.menu.entity.MenuType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MenuDto {

    private Long id;
    private Long parentId;
    private String parentName;
    private String name;
    private String code;
    private MenuType type;
    private String url;
    private String icon;
    private Integer sortOrder;
    private MenuStatus status;
    private String target;
    private String roles;
    private String description;
    private Integer depth;
    private Boolean hasChildren;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MenuDto from(Menu menu) {
        return MenuDto.builder()
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
                .roles(menu.getRoles())
                .description(menu.getDescription())
                .depth(menu.getDepth())
                .hasChildren(menu.hasChildren())
                .createdAt(menu.getCreatedAt())
                .updatedAt(menu.getUpdatedAt())
                .build();
    }
}
