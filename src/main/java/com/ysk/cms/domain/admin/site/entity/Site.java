package com.ysk.cms.domain.admin.site.entity;

import com.ysk.cms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sites")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Site extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(length = 255)
    private String domain;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SiteStatus status = SiteStatus.ACTIVE;

    @Column(columnDefinition = "JSON")
    private String settings;

    public void update(String name, String description, String domain, SiteStatus status, String settings) {
        this.name = name;
        this.description = description;
        this.domain = domain;
        this.status = status;
        this.settings = settings;
    }

    public void updateStatus(SiteStatus status) {
        this.status = status;
    }

    public boolean isActive() {
        return this.status == SiteStatus.ACTIVE;
    }
}
