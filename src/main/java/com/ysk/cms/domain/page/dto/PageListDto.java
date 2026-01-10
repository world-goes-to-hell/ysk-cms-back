package com.ysk.cms.domain.page.dto;

import com.ysk.cms.domain.page.entity.Page;
import com.ysk.cms.domain.page.entity.PageStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PageListDto {

    private Long id;
    private String slug;
    private String title;
    private PageStatus status;
    private Long parentId;
    private String parentTitle;
    private Integer sortOrder;
    private Integer childCount;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;

    public static PageListDto from(Page page) {
        return PageListDto.builder()
                .id(page.getId())
                .slug(page.getSlug())
                .title(page.getTitle())
                .status(page.getStatus())
                .parentId(page.getParent() != null ? page.getParent().getId() : null)
                .parentTitle(page.getParent() != null ? page.getParent().getTitle() : null)
                .sortOrder(page.getSortOrder())
                .childCount((int) page.getChildren().stream().filter(c -> !c.getDeleted()).count())
                .publishedAt(page.getPublishedAt())
                .createdAt(page.getCreatedAt())
                .build();
    }
}
