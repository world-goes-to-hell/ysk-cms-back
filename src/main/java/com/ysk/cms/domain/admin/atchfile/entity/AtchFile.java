package com.ysk.cms.domain.admin.atchfile.entity;

import com.ysk.cms.common.entity.BaseEntity;
import com.ysk.cms.domain.admin.article.entity.BoardArticle;
import com.ysk.cms.domain.admin.site.entity.Site;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "atch_files", indexes = {
        @Index(name = "idx_atch_file_site", columnList = "site_id"),
        @Index(name = "idx_atch_file_type", columnList = "type")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AtchFile extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id")
    private Site site;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private BoardArticle article;

    @Column(nullable = false, length = 255)
    private String originalName;

    @Column(nullable = false, length = 500)
    private String storedName;

    @Column(nullable = false, length = 1000)
    private String filePath;

    @Column(nullable = false, length = 100)
    private String mimeType;

    @Column(nullable = false)
    private Long fileSize;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AtchFileType type;

    @Column(length = 500)
    private String description;

    @Column(length = 500)
    private String altText;

    private Integer width;

    private Integer height;

    public void update(String description, String altText) {
        this.description = description;
        this.altText = altText;
    }

    public void setArticle(BoardArticle article) {
        this.article = article;
    }

    public String getExtension() {
        if (originalName == null || !originalName.contains(".")) {
            return "";
        }
        return originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();
    }
}
