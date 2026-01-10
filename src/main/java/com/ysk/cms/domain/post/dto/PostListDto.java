package com.ysk.cms.domain.post.dto;

import com.ysk.cms.domain.post.entity.Post;
import com.ysk.cms.domain.post.entity.PostStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostListDto {

    private Long id;
    private String title;
    private String author;
    private Integer viewCount;
    private Boolean isPinned;
    private Boolean isSecret;
    private PostStatus status;
    private Boolean hasAnswer;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;

    public static PostListDto from(Post post) {
        return PostListDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .author(post.getAuthor())
                .viewCount(post.getViewCount())
                .isPinned(post.getIsPinned())
                .isSecret(post.getIsSecret())
                .status(post.getStatus())
                .hasAnswer(post.getAnswer() != null && !post.getAnswer().isEmpty())
                .publishedAt(post.getPublishedAt())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
