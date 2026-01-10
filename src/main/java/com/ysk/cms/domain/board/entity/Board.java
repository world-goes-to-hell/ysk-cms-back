package com.ysk.cms.domain.board.entity;

import com.ysk.cms.common.entity.BaseEntity;
import com.ysk.cms.domain.site.entity.Site;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "boards", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"site_id", "code"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Board extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BoardType type = BoardType.NORMAL;

    @Column(nullable = false)
    @Builder.Default
    private Boolean useComment = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean useAttachment = true;

    @Column(nullable = false)
    @Builder.Default
    private Integer attachmentLimit = 5;

    @Column(nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BoardStatus status = BoardStatus.ACTIVE;

    public void update(String name, String description, BoardType type,
                       Boolean useComment, Boolean useAttachment,
                       Integer attachmentLimit, Integer sortOrder, BoardStatus status) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.useComment = useComment;
        this.useAttachment = useAttachment;
        this.attachmentLimit = attachmentLimit;
        this.sortOrder = sortOrder;
        this.status = status;
    }

    public boolean isActive() {
        return this.status == BoardStatus.ACTIVE;
    }
}
