package com.ysk.cms.domain.reply.entity;

import com.ysk.cms.common.entity.BaseEntity;
import com.ysk.cms.domain.article.entity.BoardArticle;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "board_article_replies", indexes = {
        @Index(name = "idx_reply_article", columnList = "article_id"),
        @Index(name = "idx_reply_parent", columnList = "parent_id"),
        @Index(name = "idx_reply_created", columnList = "article_id, created_at DESC")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BoardArticleReply extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private BoardArticle article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private BoardArticleReply parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BoardArticleReply> children = new ArrayList<>();

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(length = 50)
    private String author;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isSecret = false;

    public void update(String content, Boolean isSecret) {
        this.content = content;
        if (isSecret != null) {
            this.isSecret = isSecret;
        }
    }

    public boolean isReply() {
        return this.parent != null;
    }

    /**
     * 자식 댓글이 있는 경우 soft delete (내용 삭제 후 삭제 표시)
     */
    public void softDelete() {
        this.content = "";
        this.author = null;
        super.delete();
    }

    /**
     * 자식 댓글이 있는지 확인
     */
    public boolean hasChildren() {
        return this.children != null && !this.children.isEmpty()
                && this.children.stream().anyMatch(child -> !child.getDeleted());
    }
}
