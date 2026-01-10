package com.ysk.cms.domain.article.dto;

import com.ysk.cms.domain.article.entity.BoardArticle;
import com.ysk.cms.domain.article.entity.ArticleStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BoardArticleDto {

    private Long id;
    private String siteCode;
    private String boardCode;
    private String boardName;
    private String title;
    private String content;
    private String author;
    private Integer viewCount;
    private Boolean isPinned;
    private Boolean isSecret;
    private ArticleStatus status;
    private String answer;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BoardArticleDto from(BoardArticle article) {
        return BoardArticleDto.builder()
                .id(article.getId())
                .siteCode(article.getBoard().getSite().getCode())
                .boardCode(article.getBoard().getCode())
                .boardName(article.getBoard().getName())
                .title(article.getTitle())
                .content(article.getContent())
                .author(article.getAuthor())
                .viewCount(article.getViewCount())
                .isPinned(article.getIsPinned())
                .isSecret(article.getIsSecret())
                .status(article.getStatus())
                .answer(article.getAnswer())
                .publishedAt(article.getPublishedAt())
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .build();
    }
}
