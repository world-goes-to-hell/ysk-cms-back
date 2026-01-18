package com.ysk.cms.domain.user.menu.entity;

import com.ysk.cms.common.entity.BaseEntity;
import com.ysk.cms.domain.admin.site.entity.Site;
import com.ysk.cms.domain.admin.user.entity.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user_menus", indexes = {
        @Index(name = "idx_user_menu_site", columnList = "site_id"),
        @Index(name = "idx_user_menu_parent", columnList = "parent_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserMenu extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private UserMenu parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<UserMenu> children = new ArrayList<>();

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 50)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserMenuType type = UserMenuType.INTERNAL;

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
    private UserMenuStatus status = UserMenuStatus.ACTIVE;

    @Column(length = 20)
    @Builder.Default
    private String target = "_self";

    @Column(length = 500)
    private String description;

    // 메뉴 접근 가능 권한 (비어있으면 모든 사용자 접근 가능)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_menu_roles",
            joinColumns = @JoinColumn(name = "user_menu_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    public void update(String name, String code, UserMenuType type, String url,
                       String icon, Integer sortOrder, UserMenuStatus status,
                       String target, String description) {
        this.name = name;
        this.code = code;
        this.type = type;
        this.url = url;
        this.icon = icon;
        this.sortOrder = sortOrder;
        this.status = status;
        this.target = target;
        this.description = description;
    }

    public void updateParent(UserMenu parent) {
        this.parent = parent;
    }

    public void updateSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isActive() {
        return this.status == UserMenuStatus.ACTIVE;
    }

    public boolean isDirectory() {
        return this.type == UserMenuType.DIRECTORY;
    }

    public boolean hasChildren() {
        return this.children != null && !this.children.isEmpty();
    }

    public void updateRoles(Set<Role> roles) {
        this.roles.clear();
        if (roles != null) {
            this.roles.addAll(roles);
        }
    }

    public int getDepth() {
        int depth = 0;
        UserMenu current = this.parent;
        while (current != null) {
            depth++;
            current = current.getParent();
        }
        return depth;
    }
}
