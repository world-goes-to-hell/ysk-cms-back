package com.ysk.cms.config;

import com.ysk.cms.domain.admin.user.entity.*;
import com.ysk.cms.domain.admin.user.repository.PermissionRepository;
import com.ysk.cms.domain.admin.user.repository.RoleRepository;
import com.ysk.cms.domain.admin.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            log.info("초기 데이터 설정을 시작합니다.");
            initializePermissions();
            initializeRoles();
            initializeAdminUser();
            log.info("초기 데이터 설정이 완료되었습니다.");
        }
    }

    private void initializePermissions() {
        log.info("권한 데이터 초기화 중...");

        for (ResourceType resourceType : ResourceType.values()) {
            for (ActionType actionType : ActionType.values()) {
                String permissionName = resourceType.name().toLowerCase() + ":" + actionType.name().toLowerCase();
                if (!permissionRepository.existsByName(permissionName)) {
                    Permission permission = Permission.builder()
                            .name(permissionName)
                            .description(resourceType.name() + " " + actionType.name() + " 권한")
                            .resourceType(resourceType)
                            .actionType(actionType)
                            .build();
                    permissionRepository.save(permission);
                }
            }
        }
    }

    private void initializeRoles() {
        log.info("역할 데이터 초기화 중...");

        // SUPER_ADMIN - 모든 권한
        if (!roleRepository.existsByName("SUPER_ADMIN")) {
            Set<Permission> allPermissions = new HashSet<>(permissionRepository.findAll());
            Role superAdmin = Role.builder()
                    .name("SUPER_ADMIN")
                    .description("슈퍼 관리자 - 모든 권한 보유")
                    .permissions(allPermissions)
                    .build();
            roleRepository.save(superAdmin);
        }

        // SITE_ADMIN - 사이트 관리 권한
        if (!roleRepository.existsByName("SITE_ADMIN")) {
            Role siteAdmin = Role.builder()
                    .name("SITE_ADMIN")
                    .description("사이트 관리자 - 할당된 사이트 관리")
                    .permissions(new HashSet<>())
                    .build();
            addPermissionsToRole(siteAdmin,
                    "site:read", "site:update",
                    "board:create", "board:read", "board:update", "board:delete",
                    "post:create", "post:read", "post:update", "post:delete",
                    "page:create", "page:read", "page:update", "page:delete",
                    "media:create", "media:read", "media:update", "media:delete"
            );
            roleRepository.save(siteAdmin);
        }

        // EDITOR - 콘텐츠 편집 권한
        if (!roleRepository.existsByName("EDITOR")) {
            Role editor = Role.builder()
                    .name("EDITOR")
                    .description("에디터 - 콘텐츠 편집 권한")
                    .permissions(new HashSet<>())
                    .build();
            addPermissionsToRole(editor,
                    "board:read",
                    "post:create", "post:read", "post:update",
                    "page:create", "page:read", "page:update",
                    "media:create", "media:read"
            );
            roleRepository.save(editor);
        }

        // VIEWER - 읽기 전용
        if (!roleRepository.existsByName("VIEWER")) {
            Role viewer = Role.builder()
                    .name("VIEWER")
                    .description("뷰어 - 읽기 전용")
                    .permissions(new HashSet<>())
                    .build();
            addPermissionsToRole(viewer,
                    "site:read", "board:read", "post:read", "page:read", "media:read"
            );
            roleRepository.save(viewer);
        }
    }

    private void addPermissionsToRole(Role role, String... permissionNames) {
        Arrays.stream(permissionNames)
                .map(permissionRepository::findByName)
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .forEach(role::addPermission);
    }

    private void initializeAdminUser() {
        log.info("관리자 계정 초기화 중...");

        if (!userRepository.existsByUsername("admin")) {
            Role superAdminRole = roleRepository.findByName("SUPER_ADMIN")
                    .orElseThrow(() -> new RuntimeException("SUPER_ADMIN 역할을 찾을 수 없습니다."));

            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin1234"))
                    .email("admin@ysk-cms.com")
                    .name("관리자")
                    .status(UserStatus.ACTIVE)
                    .roles(new HashSet<>(Set.of(superAdminRole)))
                    .build();

            userRepository.save(admin);
            log.info("관리자 계정이 생성되었습니다. (username: admin, password: admin1234)");
        }
    }
}
