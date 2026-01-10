package com.ysk.cms.domain.menu.dto;

import com.ysk.cms.domain.menu.entity.Menu;
import com.ysk.cms.domain.menu.entity.MenuStatus;
import com.ysk.cms.domain.menu.entity.MenuType;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class MenuTreeDto {

    private Long id;
    private String name;
    private String code;
    private MenuType type;
    private String url;
    private String icon;
    private Integer sortOrder;
    private MenuStatus status;
    private String target;
    private String roles;
    private String componentPath;
    private String relatedRoutes;
    private List<MenuTreeDto> children;

    public static MenuTreeDto from(Menu menu) {
        return MenuTreeDto.builder()
                .id(menu.getId())
                .name(menu.getName())
                .code(menu.getCode())
                .type(menu.getType())
                .url(menu.getUrl())
                .icon(menu.getIcon())
                .sortOrder(menu.getSortOrder())
                .status(menu.getStatus())
                .target(menu.getTarget())
                .roles(menu.getRoles())
                .componentPath(menu.getComponentPath())
                .relatedRoutes(menu.getRelatedRoutes())
                .children(new ArrayList<>())
                .build();
    }

    public static MenuTreeDto fromWithChildren(Menu menu) {
        MenuTreeDto dto = from(menu);
        if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
            dto.children = menu.getChildren().stream()
                    .filter(child -> !child.getDeleted())
                    .map(MenuTreeDto::fromWithChildren)
                    .toList();
        }
        return dto;
    }
}
