package com.ysk.cms.domain.menu.entity;

import com.ysk.cms.common.entity.BaseEntity;
import com.ysk.cms.domain.site.entity.Site;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "admin_menus", indexes = {
        @Index(name = "idx_admin_menu_site", columnList = "site_id"),
        @Index(name = "idx_admin_menu_parent", columnList = "parent_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Menu extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Menu parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<Menu> children = new ArrayList<>();

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 50)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MenuType type = MenuType.INTERNAL;

    @Column(length = 500)
    private String url;

    @Column(length = 50)
    private String icon;

    @Column(nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MenuStatus status = MenuStatus.ACTIVE;

    @Column(length = 20)
    @Builder.Default
    private String target = "_self";

    @Column(length = 500)
    private String roles;

    @Column(length = 500)
    private String description;

    public void update(String name, String code, MenuType type, String url,
                       String icon, Integer sortOrder, MenuStatus status,
                       String target, String roles, String description) {
        this.name = name;
        this.code = code;
        this.type = type;
        this.url = url;
        this.icon = icon;
        this.sortOrder = sortOrder;
        this.status = status;
        this.target = target;
        this.roles = roles;
        this.description = description;
    }

    public void updateParent(Menu parent) {
        this.parent = parent;
    }

    public void updateSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isActive() {
        return this.status == MenuStatus.ACTIVE;
    }

    public boolean isDirectory() {
        return this.type == MenuType.DIRECTORY;
    }

    public boolean hasChildren() {
        return this.children != null && !this.children.isEmpty();
    }

    public int getDepth() {
        int depth = 0;
        Menu current = this.parent;
        while (current != null) {
            depth++;
            current = current.getParent();
        }
        return depth;
    }
}
