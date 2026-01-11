package com.ysk.cms.domain.board.dto;

import com.ysk.cms.domain.board.entity.Board;
import com.ysk.cms.domain.board.entity.BoardStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BoardDto {

    private Long id;
    private String siteCode;
    private String code;
    private String name;
    private String description;
    private String typeCode;
    private Boolean useComment;
    private Boolean useAttachment;
    private Integer attachmentLimit;
    private Boolean useSecret;
    private Boolean usePinned;
    private Integer sortOrder;
    private BoardStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BoardDto from(Board board) {
        return BoardDto.builder()
                .id(board.getId())
                .siteCode(board.getSite().getCode())
                .code(board.getCode())
                .name(board.getName())
                .description(board.getDescription())
                .typeCode(board.getTypeCode())
                .useComment(board.getUseComment())
                .useAttachment(board.getUseAttachment())
                .attachmentLimit(board.getAttachmentLimit())
                .useSecret(board.getUseSecret())
                .usePinned(board.getUsePinned())
                .sortOrder(board.getSortOrder())
                .status(board.getStatus())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .build();
    }
}
