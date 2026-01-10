package com.ysk.cms.domain.activity.entity;

import com.ysk.cms.domain.site.entity.Site;
import com.ysk.cms.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "activity_logs", indexes = {
        @Index(name = "idx_activity_created", columnList = "created_at DESC"),
        @Index(name = "idx_activity_user", columnList = "user_id"),
        @Index(name = "idx_activity_site", columnList = "site_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id")
    private Site site;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType activityType;

    @Column(nullable = false, length = 50)
    private String targetType;

    private Long targetId;

    @Column(length = 200)
    private String targetName;

    @Column(length = 500)
    private String description;

    @Column(length = 50)
    private String ipAddress;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public String getIcon() {
        return switch (targetType.toUpperCase()) {
            case "POST", "PAGE" -> "Document";
            case "USER" -> "User";
            case "BOARD", "SITE" -> "Setting";
            case "MEDIA" -> "Picture";
            default -> switch (activityType) {
                case DELETE -> "Delete";
                case CREATE -> "Plus";
                case UPDATE -> "Edit";
                default -> "Document";
            };
        };
    }

    public String getActionText() {
        String target = switch (targetType.toUpperCase()) {
            case "POST" -> "게시글";
            case "PAGE" -> "페이지";
            case "BOARD" -> "게시판";
            case "USER" -> "사용자";
            case "SITE" -> "사이트";
            case "MEDIA" -> "미디어";
            default -> targetType;
        };

        return switch (activityType) {
            case CREATE -> target + " 등록";
            case UPDATE -> target + " 수정";
            case DELETE -> target + " 삭제";
            case LOGIN -> "로그인";
            case LOGOUT -> "로그아웃";
            case VIEW -> target + " 조회";
            case PUBLISH -> target + " 발행";
            case UPLOAD -> "파일 업로드";
        };
    }
}
