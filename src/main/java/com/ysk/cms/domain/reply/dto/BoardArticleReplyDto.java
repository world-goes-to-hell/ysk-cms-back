package com.ysk.cms.domain.reply.dto;

import com.ysk.cms.domain.reply.entity.BoardArticleReply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardArticleReplyDto {

    private static final String DELETED_MESSAGE = "삭제된 댓글입니다.";

    private Long id;
    private Long articleId;
    private Long parentId;
    private String content;
    private String author;
    private Boolean isSecret;
    private Boolean deleted;
    private Integer depth;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private List<BoardArticleReplyDto> children;

    public static BoardArticleReplyDto from(BoardArticleReply reply) {
        boolean isDeleted = reply.getDeleted();

        // 자식 댓글 필터링: 삭제되지 않은 댓글 또는 삭제되었지만 자식이 있는 댓글
        List<BoardArticleReplyDto> childDtos = null;
        if (reply.getChildren() != null && !reply.getChildren().isEmpty()) {
            childDtos = reply.getChildren().stream()
                    .filter(child -> !child.getDeleted() || hasActiveDescendants(child))
                    .map(BoardArticleReplyDto::from)
                    .collect(Collectors.toList());
            if (childDtos.isEmpty()) {
                childDtos = null;
            }
        }

        return BoardArticleReplyDto.builder()
                .id(reply.getId())
                .articleId(reply.getArticle().getId())
                .parentId(reply.getParent() != null ? reply.getParent().getId() : null)
                .content(isDeleted ? DELETED_MESSAGE : reply.getContent())
                .author(isDeleted ? null : reply.getAuthor())
                .isSecret(reply.getIsSecret())
                .deleted(isDeleted)
                .depth(reply.getDepth())
                .createdAt(reply.getCreatedAt())
                .updatedAt(reply.getUpdatedAt())
                .createdBy(isDeleted ? null : reply.getCreatedBy())
                .children(childDtos)
                .build();
    }

    /**
     * 플랫 구조용 변환 (자식 댓글 제외)
     */
    public static BoardArticleReplyDto fromFlat(BoardArticleReply reply) {
        boolean isDeleted = reply.getDeleted();

        return BoardArticleReplyDto.builder()
                .id(reply.getId())
                .articleId(reply.getArticle().getId())
                .parentId(reply.getParent() != null ? reply.getParent().getId() : null)
                .content(isDeleted ? DELETED_MESSAGE : reply.getContent())
                .author(isDeleted ? null : reply.getAuthor())
                .isSecret(reply.getIsSecret())
                .deleted(isDeleted)
                .depth(reply.getDepth())
                .createdAt(reply.getCreatedAt())
                .updatedAt(reply.getUpdatedAt())
                .createdBy(isDeleted ? null : reply.getCreatedBy())
                .children(null)
                .build();
    }

    /**
     * 활성 자손(삭제되지 않은 자식 댓글)이 있는지 재귀적으로 확인
     */
    private static boolean hasActiveDescendants(BoardArticleReply reply) {
        if (reply.getChildren() == null || reply.getChildren().isEmpty()) {
            return false;
        }
        return reply.getChildren().stream()
                .anyMatch(child -> !child.getDeleted() || hasActiveDescendants(child));
    }
}
