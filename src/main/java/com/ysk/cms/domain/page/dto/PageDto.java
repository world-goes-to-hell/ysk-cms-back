package com.ysk.cms.domain.page.dto;

import com.ysk.cms.domain.page.entity.Page;
import com.ysk.cms.domain.page.entity.PageStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PageDto {

    private Long id;
    private String siteCode;
    private String slug;
    private String title;
    private String content;
    private String metaDescription;
    private String metaKeywords;
    private PageStatus status;
    private LocalDateTime publishedAt;
    private Long parentId;
    private String parentTitle;
    private Integer sortOrder;
    private List<PageDto> children;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PageDto from(Page page) {
        return PageDto.builder()
                .id(page.getId())
                .siteCode(page.getSite().getCode())
                .slug(page.getSlug())
                .title(page.getTitle())
                .content(page.getContent())
                .metaDescription(page.getMetaDescription())
                .metaKeywords(page.getMetaKeywords())
                .status(page.getStatus())
                .publishedAt(page.getPublishedAt())
                .parentId(page.getParent() != null ? page.getParent().getId() : null)
                .parentTitle(page.getParent() != null ? page.getParent().getTitle() : null)
                .sortOrder(page.getSortOrder())
                .createdAt(page.getCreatedAt())
                .updatedAt(page.getUpdatedAt())
                .build();
    }

    public static PageDto fromWithChildren(Page page) {
        return PageDto.builder()
                .id(page.getId())
                .siteCode(page.getSite().getCode())
                .slug(page.getSlug())
                .title(page.getTitle())
                .content(page.getContent())
                .metaDescription(page.getMetaDescription())
                .metaKeywords(page.getMetaKeywords())
                .status(page.getStatus())
                .publishedAt(page.getPublishedAt())
                .parentId(page.getParent() != null ? page.getParent().getId() : null)
                .parentTitle(page.getParent() != null ? page.getParent().getTitle() : null)
                .sortOrder(page.getSortOrder())
                .children(page.getChildren().stream()
                        .filter(child -> !child.getDeleted())
                        .map(PageDto::fromWithChildren)
                        .toList())
                .createdAt(page.getCreatedAt())
                .updatedAt(page.getUpdatedAt())
                .build();
    }
}
