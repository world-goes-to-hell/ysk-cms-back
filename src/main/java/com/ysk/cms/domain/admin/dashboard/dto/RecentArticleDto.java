package com.ysk.cms.domain.admin.dashboard.dto;

import com.ysk.cms.domain.admin.article.entity.BoardArticle;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class RecentArticleDto {

    private Long id;
    private String title;
    private String board;
    private String author;
    private String date;
    private Integer views;
    private Boolean isNew;

    public static RecentArticleDto from(BoardArticle article) {
        LocalDateTime createdAt = article.getCreatedAt();
        boolean isNewArticle = createdAt != null &&
                createdAt.toLocalDate().isAfter(LocalDate.now().minusDays(2));

        return RecentArticleDto.builder()
                .id(article.getId())
                .title(article.getTitle())
                .board(article.getBoard().getName())
                .author(article.getAuthor() != null ? article.getAuthor() : "관리자")
                .date(createdAt != null ? createdAt.toLocalDate().toString() : "")
                .views(article.getViewCount())
                .isNew(isNewArticle)
                .build();
    }
}
