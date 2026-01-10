package com.ysk.cms.domain.post.dto;

import com.ysk.cms.domain.post.entity.Post;
import com.ysk.cms.domain.post.entity.PostStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostDto {

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
    private PostStatus status;
    private String answer;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PostDto from(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .siteCode(post.getBoard().getSite().getCode())
                .boardCode(post.getBoard().getCode())
                .boardName(post.getBoard().getName())
                .title(post.getTitle())
                .content(post.getContent())
                .author(post.getAuthor())
                .viewCount(post.getViewCount())
                .isPinned(post.getIsPinned())
                .isSecret(post.getIsSecret())
                .status(post.getStatus())
                .answer(post.getAnswer())
                .publishedAt(post.getPublishedAt())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
