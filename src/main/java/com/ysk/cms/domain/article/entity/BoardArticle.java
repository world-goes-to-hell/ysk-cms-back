package com.ysk.cms.domain.article.entity;

import com.ysk.cms.common.entity.BaseEntity;
import com.ysk.cms.domain.board.entity.Board;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "board_articles", indexes = {
        @Index(name = "idx_article_board_created", columnList = "board_id, created_at DESC")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BoardArticle extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column(nullable = false, length = 300)
    private String title;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(length = 50)
    private String author;

    @Column(nullable = false)
    @Builder.Default
    private Integer viewCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isPinned = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isSecret = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ArticleStatus status = ArticleStatus.DRAFT;

    private LocalDateTime publishedAt;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String answer;

    public void update(String title, String content, String author,
                       Boolean isPinned, Boolean isSecret, ArticleStatus status, String answer) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.isPinned = isPinned;
        this.isSecret = isSecret;
        this.status = status;
        this.answer = answer;

        if (status == ArticleStatus.PUBLISHED && this.publishedAt == null) {
            this.publishedAt = LocalDateTime.now();
        }
    }

    public void publish() {
        this.status = ArticleStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public boolean isPublished() {
        return this.status == ArticleStatus.PUBLISHED;
    }
}
