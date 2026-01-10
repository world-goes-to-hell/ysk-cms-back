package com.ysk.cms.domain.article.dto;

import com.ysk.cms.domain.article.entity.BoardArticle;
import com.ysk.cms.domain.article.entity.ArticleStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BoardArticleListDto {

    private Long id;
    private String title;
    private String author;
    private Integer viewCount;
    private Boolean isPinned;
    private Boolean isSecret;
    private ArticleStatus status;
    private Boolean hasAnswer;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;

    public static BoardArticleListDto from(BoardArticle article) {
        return BoardArticleListDto.builder()
                .id(article.getId())
                .title(article.getTitle())
                .author(article.getAuthor())
                .viewCount(article.getViewCount())
                .isPinned(article.getIsPinned())
                .isSecret(article.getIsSecret())
                .status(article.getStatus())
                .hasAnswer(article.getAnswer() != null && !article.getAnswer().isEmpty())
                .publishedAt(article.getPublishedAt())
                .createdAt(article.getCreatedAt())
                .build();
    }
}
