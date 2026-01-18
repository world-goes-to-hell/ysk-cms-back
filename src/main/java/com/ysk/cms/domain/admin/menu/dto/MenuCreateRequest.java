package com.ysk.cms.domain.admin.menu.dto;

import com.ysk.cms.domain.admin.menu.entity.MenuStatus;
import com.ysk.cms.domain.admin.menu.entity.MenuType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuCreateRequest {

    private Long parentId;

    @NotBlank(message = "메뉴명은 필수입니다")
    @Size(max = 100, message = "메뉴명은 100자 이하로 입력해주세요")
    private String name;

    @Size(max = 50, message = "메뉴 코드는 50자 이하로 입력해주세요")
    private String code;

    private MenuType type = MenuType.INTERNAL;

    @Size(max = 500, message = "URL은 500자 이하로 입력해주세요")
    private String url;

    @Size(max = 50, message = "아이콘은 50자 이하로 입력해주세요")
    private String icon;

    private Integer sortOrder;

    private MenuStatus status = MenuStatus.ACTIVE;

    private String target = "_self";

    @Size(max = 500, message = "권한은 500자 이하로 입력해주세요")
    private String roles;

    @Size(max = 500, message = "설명은 500자 이하로 입력해주세요")
    private String description;

    @Size(max = 255, message = "컴포넌트 경로는 255자 이하로 입력해주세요")
    private String componentPath;

    private String relatedRoutes;
}
