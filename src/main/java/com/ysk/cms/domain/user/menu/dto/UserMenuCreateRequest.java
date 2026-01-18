package com.ysk.cms.domain.user.menu.dto;

import com.ysk.cms.domain.user.menu.entity.UserMenuStatus;
import com.ysk.cms.domain.user.menu.entity.UserMenuType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserMenuCreateRequest {

    private Long parentId;

    @NotBlank(message = "메뉴명은 필수입니다")
    @Size(max = 100, message = "메뉴명은 100자 이하로 입력해주세요")
    private String name;

    @Size(max = 50, message = "메뉴 코드는 50자 이하로 입력해주세요")
    private String code;

    private UserMenuType type = UserMenuType.INTERNAL;

    @Size(max = 500, message = "URL은 500자 이하로 입력해주세요")
    private String url;

    @Size(max = 50, message = "아이콘은 50자 이하로 입력해주세요")
    private String icon;

    private Integer sortOrder;

    private UserMenuStatus status = UserMenuStatus.ACTIVE;

    private String target = "_self";

    @Size(max = 500, message = "설명은 500자 이하로 입력해주세요")
    private String description;

    // 접근 가능 권한 ID 목록 (비어있으면 모든 사용자 접근 가능)
    private Set<Long> roleIds;
}
