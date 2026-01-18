package com.ysk.cms.domain.admin.board.entity;

import com.ysk.cms.common.entity.BaseEntity;
import com.ysk.cms.domain.admin.site.entity.Site;
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

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String typeCode = "NORMAL";

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
    private Boolean useSecret = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean usePinned = false;

    @Column(nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BoardStatus status = BoardStatus.ACTIVE;

    public void update(String name, String description, String typeCode,
                       Boolean useComment, Boolean useAttachment,
                       Integer attachmentLimit, Boolean useSecret, Boolean usePinned,
                       Integer sortOrder, BoardStatus status) {
        this.name = name;
        this.description = description;
        this.typeCode = typeCode;
        this.useComment = useComment;
        this.useAttachment = useAttachment;
        this.attachmentLimit = attachmentLimit;
        this.useSecret = useSecret;
        this.usePinned = usePinned;
        this.sortOrder = sortOrder;
        this.status = status;
    }

    public boolean isActive() {
        return this.status == BoardStatus.ACTIVE;
    }
}
