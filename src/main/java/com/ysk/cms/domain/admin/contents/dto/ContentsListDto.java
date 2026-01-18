package com.ysk.cms.domain.admin.contents.dto;

import com.ysk.cms.domain.admin.contents.entity.Contents;
import com.ysk.cms.domain.admin.contents.entity.ContentsStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ContentsListDto {

    private Long id;
    private String slug;
    private String title;
    private ContentsStatus status;
    private Long parentId;
    private String parentTitle;
    private Integer sortOrder;
    private Integer childCount;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;

    public static ContentsListDto from(Contents contents) {
        return ContentsListDto.builder()
                .id(contents.getId())
                .slug(contents.getSlug())
                .title(contents.getTitle())
                .status(contents.getStatus())
                .parentId(contents.getParent() != null ? contents.getParent().getId() : null)
                .parentTitle(contents.getParent() != null ? contents.getParent().getTitle() : null)
                .sortOrder(contents.getSortOrder())
                .childCount((int) contents.getChildren().stream().filter(c -> !c.getDeleted()).count())
                .publishedAt(contents.getPublishedAt())
                .createdAt(contents.getCreatedAt())
                .build();
    }
}
