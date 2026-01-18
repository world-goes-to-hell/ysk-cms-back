package com.ysk.cms.domain.admin.board.dto;

import com.ysk.cms.domain.admin.board.entity.BoardTypeEntity;
import com.ysk.cms.domain.admin.board.entity.BoardTypeStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BoardTypeDto {
    private Long id;
    private String siteCode;
    private String code;
    private String name;
    private String description;
    private String icon;
    private String color;
    private String bgColor;
    private Integer sortOrder;
    private BoardTypeStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BoardTypeDto from(BoardTypeEntity entity) {
        return BoardTypeDto.builder()
                .id(entity.getId())
                .siteCode(entity.getSite().getCode())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .icon(entity.getIcon())
                .color(entity.getColor())
                .bgColor(entity.getBgColor())
                .sortOrder(entity.getSortOrder())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
