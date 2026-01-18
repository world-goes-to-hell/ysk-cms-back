package com.ysk.cms.domain.admin.menu.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MenuSortRequest {

    @NotNull(message = "정렬 목록은 필수입니다")
    private List<MenuSortItem> items;

    @Getter
    @Setter
    public static class MenuSortItem {
        @NotNull(message = "메뉴 ID는 필수입니다")
        private Long id;

        private Long parentId;

        @NotNull(message = "정렬 순서는 필수입니다")
        private Integer sortOrder;
    }
}
