package com.ysk.cms.domain.dashboard.dto;

import com.ysk.cms.domain.post.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class RecentPostDto {

    private Long id;
    private String title;
    private String board;
    private String author;
    private String date;
    private Integer views;
    private Boolean isNew;

    public static RecentPostDto from(Post post) {
        LocalDateTime createdAt = post.getCreatedAt();
        boolean isNewPost = createdAt != null &&
                createdAt.toLocalDate().isAfter(LocalDate.now().minusDays(2));

        return RecentPostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .board(post.getBoard().getName())
                .author(post.getAuthor() != null ? post.getAuthor() : "관리자")
                .date(createdAt != null ? createdAt.toLocalDate().toString() : "")
                .views(post.getViewCount())
                .isNew(isNewPost)
                .build();
    }
}
