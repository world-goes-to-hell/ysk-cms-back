package com.ysk.cms.domain.user.menu.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserMenuSortRequest {

    @NotNull(message = "정렬 목록은 필수입니다")
    private List<UserMenuSortItem> items;

    @Getter
    @Setter
    public static class UserMenuSortItem {
        @NotNull(message = "메뉴 ID는 필수입니다")
        private Long id;

        private Long parentId;

        @NotNull(message = "정렬 순서는 필수입니다")
        private Integer sortOrder;
    }
}
