package com.ysk.cms.domain.user.entity;

import com.ysk.cms.common.entity.BaseEntity;
import com.ysk.cms.domain.site.entity.Site;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    private LocalDateTime lastLoginAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_sites",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "site_id")
    )
    @Builder.Default
    private Set<Site> accessibleSites = new HashSet<>();

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void updateProfile(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public void updateStatus(UserStatus status) {
        this.status = status;
    }

    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    public void clearRoles() {
        this.roles.clear();
    }

    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    public boolean isLocked() {
        return this.status == UserStatus.LOCKED;
    }

    public void addSite(Site site) {
        this.accessibleSites.add(site);
    }

    public void removeSite(Site site) {
        this.accessibleSites.remove(site);
    }

    public void clearSites() {
        this.accessibleSites.clear();
    }

    public boolean hasAccessToSite(String siteCode) {
        return this.accessibleSites.stream()
                .anyMatch(site -> site.getCode().equals(siteCode));
    }

    public boolean isSuperAdmin() {
        return this.roles.stream()
                .anyMatch(role -> "SUPER_ADMIN".equals(role.getName()));
    }
}
