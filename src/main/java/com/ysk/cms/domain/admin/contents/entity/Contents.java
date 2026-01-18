package com.ysk.cms.domain.admin.contents.entity;

import com.ysk.cms.common.entity.BaseEntity;
import com.ysk.cms.domain.admin.site.entity.Site;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contents", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"site_id", "slug"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Contents extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @Column(nullable = false, length = 100)
    private String slug;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(length = 300)
    private String metaDescription;

    @Column(length = 500)
    private String metaKeywords;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ContentsStatus status = ContentsStatus.DRAFT;

    private LocalDateTime publishedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Contents parent;

    @OneToMany(mappedBy = "parent")
    @Builder.Default
    private List<Contents> children = new ArrayList<>();

    @Column(nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    public void update(String title, String content, String slug,
                       String metaDescription, String metaKeywords,
                       ContentsStatus status, Integer sortOrder) {
        this.title = title;
        this.content = content;
        this.slug = slug;
        this.metaDescription = metaDescription;
        this.metaKeywords = metaKeywords;
        this.status = status;
        this.sortOrder = sortOrder;

        if (status == ContentsStatus.PUBLISHED && this.publishedAt == null) {
            this.publishedAt = LocalDateTime.now();
        }
    }

    public void updateParent(Contents parent) {
        this.parent = parent;
    }

    public void publish() {
        this.status = ContentsStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
    }

    public boolean isPublished() {
        return this.status == ContentsStatus.PUBLISHED;
    }
}
