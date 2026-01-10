package com.ysk.cms.domain.page.entity;

import com.ysk.cms.common.entity.BaseEntity;
import com.ysk.cms.domain.site.entity.Site;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pages", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"site_id", "slug"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Page extends BaseEntity {

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
    private PageStatus status = PageStatus.DRAFT;

    private LocalDateTime publishedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Page parent;

    @OneToMany(mappedBy = "parent")
    @Builder.Default
    private List<Page> children = new ArrayList<>();

    @Column(nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    public void update(String title, String content, String slug,
                       String metaDescription, String metaKeywords,
                       PageStatus status, Integer sortOrder) {
        this.title = title;
        this.content = content;
        this.slug = slug;
        this.metaDescription = metaDescription;
        this.metaKeywords = metaKeywords;
        this.status = status;
        this.sortOrder = sortOrder;

        if (status == PageStatus.PUBLISHED && this.publishedAt == null) {
            this.publishedAt = LocalDateTime.now();
        }
    }

    public void updateParent(Page parent) {
        this.parent = parent;
    }

    public void publish() {
        this.status = PageStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
    }

    public boolean isPublished() {
        return this.status == PageStatus.PUBLISHED;
    }
}
