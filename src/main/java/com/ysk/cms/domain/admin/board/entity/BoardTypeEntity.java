package com.ysk.cms.domain.admin.board.entity;

import com.ysk.cms.common.entity.BaseEntity;
import com.ysk.cms.domain.admin.site.entity.Site;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "board_types", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"site_id", "code"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BoardTypeEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(length = 100)
    @Builder.Default
    private String icon = "mdi-file-document-outline";

    @Column(length = 20)
    @Builder.Default
    private String color = "#6366f1";

    @Column(length = 50)
    @Builder.Default
    private String bgColor = "rgba(99, 102, 241, 0.1)";

    @Column(nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BoardTypeStatus status = BoardTypeStatus.ACTIVE;

    public void update(String name, String description, String icon, String color, String bgColor,
                       Integer sortOrder, BoardTypeStatus status) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.color = color;
        this.bgColor = bgColor;
        this.sortOrder = sortOrder;
        this.status = status;
    }

    public boolean isActive() {
        return this.status == BoardTypeStatus.ACTIVE;
    }
}
