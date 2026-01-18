package com.ysk.cms.domain.admin.contents.dto;

import com.ysk.cms.domain.admin.contents.entity.Contents;
import com.ysk.cms.domain.admin.contents.entity.ContentsStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ContentsDto {

    private Long id;
    private String siteCode;
    private String slug;
    private String title;
    private String content;
    private String metaDescription;
    private String metaKeywords;
    private ContentsStatus status;
    private LocalDateTime publishedAt;
    private Long parentId;
    private String parentTitle;
    private Integer sortOrder;
    private List<ContentsDto> children;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ContentsDto from(Contents contents) {
        return ContentsDto.builder()
                .id(contents.getId())
                .siteCode(contents.getSite().getCode())
                .slug(contents.getSlug())
                .title(contents.getTitle())
                .content(contents.getContent())
                .metaDescription(contents.getMetaDescription())
                .metaKeywords(contents.getMetaKeywords())
                .status(contents.getStatus())
                .publishedAt(contents.getPublishedAt())
                .parentId(contents.getParent() != null ? contents.getParent().getId() : null)
                .parentTitle(contents.getParent() != null ? contents.getParent().getTitle() : null)
                .sortOrder(contents.getSortOrder())
                .createdAt(contents.getCreatedAt())
                .updatedAt(contents.getUpdatedAt())
                .build();
    }

    public static ContentsDto fromWithChildren(Contents contents) {
        return ContentsDto.builder()
                .id(contents.getId())
                .siteCode(contents.getSite().getCode())
                .slug(contents.getSlug())
                .title(contents.getTitle())
                .content(contents.getContent())
                .metaDescription(contents.getMetaDescription())
                .metaKeywords(contents.getMetaKeywords())
                .status(contents.getStatus())
                .publishedAt(contents.getPublishedAt())
                .parentId(contents.getParent() != null ? contents.getParent().getId() : null)
                .parentTitle(contents.getParent() != null ? contents.getParent().getTitle() : null)
                .sortOrder(contents.getSortOrder())
                .children(contents.getChildren().stream()
                        .filter(child -> !child.getDeleted())
                        .map(ContentsDto::fromWithChildren)
                        .toList())
                .createdAt(contents.getCreatedAt())
                .updatedAt(contents.getUpdatedAt())
                .build();
    }
}
