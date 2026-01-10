package com.ysk.cms.domain.dev.controller;

import com.ysk.cms.common.dto.ApiResponse;
import com.ysk.cms.domain.activity.entity.ActivityLog;
import com.ysk.cms.domain.activity.entity.ActivityType;
import com.ysk.cms.domain.activity.repository.ActivityLogRepository;
import com.ysk.cms.domain.board.entity.Board;
import com.ysk.cms.domain.board.entity.BoardStatus;
import com.ysk.cms.domain.board.entity.BoardType;
import com.ysk.cms.domain.board.repository.BoardRepository;
import com.ysk.cms.domain.menu.entity.Menu;
import com.ysk.cms.domain.menu.entity.MenuStatus;
import com.ysk.cms.domain.menu.entity.MenuType;
import com.ysk.cms.domain.menu.repository.MenuRepository;
import com.ysk.cms.domain.post.entity.Post;
import com.ysk.cms.domain.post.entity.PostStatus;
import com.ysk.cms.domain.post.repository.PostRepository;
import com.ysk.cms.domain.site.entity.Site;
import com.ysk.cms.domain.site.entity.SiteStatus;
import com.ysk.cms.domain.site.repository.SiteRepository;
import com.ysk.cms.domain.user.entity.User;
import com.ysk.cms.domain.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@Tag(name = "개발용", description = "개발/테스트용 API (운영환경 사용 금지)")
@RestController
@RequestMapping("/api/dev")
@RequiredArgsConstructor
public class DevController {

    private final SiteRepository siteRepository;
    private final BoardRepository boardRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ActivityLogRepository activityLogRepository;
    private final MenuRepository menuRepository;

    @Operation(summary = "샘플 데이터 생성", description = "대시보드 테스트용 샘플 데이터를 생성합니다.")
    @PostMapping("/init-sample-data")
    public ApiResponse<Map<String, Object>> initSampleData() {
        // 1. 사이트 생성 (없으면)
        Site site = siteRepository.findByCode("main")
                .orElseGet(() -> siteRepository.save(Site.builder()
                        .code("main")
                        .name("메인 사이트")
                        .description("YSK CMS 메인 사이트")
                        .domain("localhost")
                        .status(SiteStatus.ACTIVE)
                        .build()));

        // 2. 게시판 생성
        Board noticeBoard = boardRepository.findBySiteCodeAndCode("main", "notice")
                .orElseGet(() -> boardRepository.save(Board.builder()
                        .site(site)
                        .code("notice")
                        .name("공지사항")
                        .description("시스템 공지사항")
                        .type(BoardType.NOTICE)
                        .status(BoardStatus.ACTIVE)
                        .build()));

        Board freeBoard = boardRepository.findBySiteCodeAndCode("main", "free")
                .orElseGet(() -> boardRepository.save(Board.builder()
                        .site(site)
                        .code("free")
                        .name("자유게시판")
                        .description("자유롭게 글을 작성하세요")
                        .type(BoardType.NORMAL)
                        .useComment(true)
                        .status(BoardStatus.ACTIVE)
                        .build()));

        Board qnaBoard = boardRepository.findBySiteCodeAndCode("main", "qna")
                .orElseGet(() -> boardRepository.save(Board.builder()
                        .site(site)
                        .code("qna")
                        .name("Q&A")
                        .description("질문과 답변")
                        .type(BoardType.QNA)
                        .status(BoardStatus.ACTIVE)
                        .build()));

        // 3. 샘플 게시글 생성
        int postCount = 0;

        // 공지사항
        postCount += createPostIfNotExists(noticeBoard, "2024년 시스템 업데이트 안내",
                "<p>안녕하세요. 2024년 시스템 업데이트 내용을 안내드립니다.</p><ul><li>UI/UX 개선</li><li>성능 최적화</li><li>보안 강화</li></ul>",
                "관리자", true, 342);

        postCount += createPostIfNotExists(noticeBoard, "신규 기능 사용 가이드",
                "<p>새롭게 추가된 기능들에 대한 사용 가이드입니다.</p>",
                "관리자", false, 256);

        postCount += createPostIfNotExists(noticeBoard, "서버 점검 일정 안내 (1/15)",
                "<p>1월 15일 02:00 ~ 06:00 서버 점검이 예정되어 있습니다.</p>",
                "운영팀", false, 189);

        // 자유게시판
        postCount += createPostIfNotExists(freeBoard, "오늘 날씨가 좋네요",
                "<p>봄이 오는 것 같습니다. 다들 좋은 하루 보내세요!</p>",
                "날씨요정", false, 892);

        postCount += createPostIfNotExists(freeBoard, "맛집 추천해주세요",
                "<p>강남역 근처 맛집 추천 부탁드립니다. 회식 장소를 찾고 있어요.</p>",
                "먹보", false, 156);

        // Q&A
        postCount += createPostIfNotExists(qnaBoard, "비밀번호를 잊어버렸어요",
                "<p>비밀번호를 잊어버린 경우 어떻게 해야 하나요?</p>",
                "초보자", false, 521);

        // 4. 관리자 찾기
        User admin = userRepository.findByUsername("admin").orElse(null);

        // 5. 활동 로그 생성
        int activityCount = 0;
        if (admin != null) {
            activityCount += createActivityLog(admin, site, ActivityType.LOGIN, "USER", admin.getId(), "admin", 5);
            activityCount += createActivityLog(admin, site, ActivityType.CREATE, "POST", 1L, "2024년 시스템 업데이트 안내", 12);
            activityCount += createActivityLog(admin, site, ActivityType.UPDATE, "USER", 2L, "홍길동", 60);
            activityCount += createActivityLog(admin, site, ActivityType.CREATE, "BOARD", 1L, "공지사항", 120);
            activityCount += createActivityLog(admin, site, ActivityType.DELETE, "POST", 99L, "테스트 게시글", 180);
            activityCount += createActivityLog(admin, site, ActivityType.CREATE, "POST", 2L, "신규 기능 사용 가이드", 240);
        }

        return ApiResponse.success("샘플 데이터 생성 완료", Map.of(
                "site", site.getName(),
                "boards", 3,
                "posts", postCount,
                "activities", activityCount
        ));
    }

    private int createPostIfNotExists(Board board, String title, String content, String author, boolean isPinned, int viewCount) {
        // 같은 제목의 게시글이 있으면 생성하지 않음
        boolean exists = postRepository.findBySiteCodeAndBoardCode(
                board.getSite().getCode(), board.getCode(),
                org.springframework.data.domain.PageRequest.of(0, 100))
                .stream()
                .anyMatch(p -> p.getTitle().equals(title));

        if (exists) {
            return 0;
        }

        Post post = Post.builder()
                .board(board)
                .title(title)
                .content(content)
                .author(author)
                .isPinned(isPinned)
                .status(PostStatus.PUBLISHED)
                .build();
        post.publish();

        // viewCount 설정 (리플렉션 사용)
        try {
            java.lang.reflect.Field viewCountField = Post.class.getDeclaredField("viewCount");
            viewCountField.setAccessible(true);
            viewCountField.set(post, viewCount);
        } catch (Exception ignored) {}

        postRepository.save(post);
        return 1;
    }

    private int createActivityLog(User user, Site site, ActivityType type, String targetType, Long targetId, String targetName, int minutesAgo) {
        ActivityLog log = ActivityLog.builder()
                .user(user)
                .site(site)
                .activityType(type)
                .targetType(targetType)
                .targetId(targetId)
                .targetName(targetName)
                .ipAddress("127.0.0.1")
                .build();

        // createdAt 설정
        try {
            java.lang.reflect.Field createdAtField = ActivityLog.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(log, LocalDateTime.now().minusMinutes(minutesAgo));
        } catch (Exception ignored) {}

        activityLogRepository.save(log);
        return 1;
    }

    @Operation(summary = "관리자 메뉴 샘플 데이터 생성", description = "관리자 메뉴 테스트용 샘플 데이터를 생성합니다.")
    @PostMapping("/init-admin-menus")
    public ApiResponse<Map<String, Object>> initAdminMenus() {
        // 사이트 확인/생성
        Site site = siteRepository.findByCode("main")
                .orElseGet(() -> siteRepository.save(Site.builder()
                        .code("main")
                        .name("메인 사이트")
                        .description("YSK CMS 메인 사이트")
                        .domain("localhost")
                        .status(SiteStatus.ACTIVE)
                        .build()));

        // 기존 메뉴가 있으면 스킵
        if (!menuRepository.findBySiteCode("main").isEmpty()) {
            return ApiResponse.success("이미 메뉴 데이터가 존재합니다.", Map.of(
                    "site", site.getName(),
                    "menus", menuRepository.findBySiteCode("main").size()
            ));
        }

        int menuCount = 0;

        // 1. 대시보드 (루트 메뉴)
        Menu dashboard = menuRepository.save(Menu.builder()
                .site(site)
                .name("대시보드")
                .code("dashboard")
                .type(MenuType.INTERNAL)
                .url("/dashboard")
                .icon("mdi-view-dashboard")
                .sortOrder(1)
                .status(MenuStatus.ACTIVE)
                .build());
        menuCount++;

        // 2. 콘텐츠 관리 (디렉토리)
        Menu content = menuRepository.save(Menu.builder()
                .site(site)
                .name("콘텐츠 관리")
                .code("content")
                .type(MenuType.DIRECTORY)
                .icon("mdi-file-document-multiple")
                .sortOrder(2)
                .status(MenuStatus.ACTIVE)
                .build());
        menuCount++;

        // 2-1. 게시판 관리
        menuRepository.save(Menu.builder()
                .site(site)
                .parent(content)
                .name("게시판 관리")
                .code("boards")
                .type(MenuType.INTERNAL)
                .url("/boards")
                .icon("mdi-bulletin-board")
                .sortOrder(1)
                .status(MenuStatus.ACTIVE)
                .build());
        menuCount++;

        // 2-2. 게시글 관리
        menuRepository.save(Menu.builder()
                .site(site)
                .parent(content)
                .name("게시글 관리")
                .code("posts")
                .type(MenuType.INTERNAL)
                .url("/posts")
                .icon("mdi-post")
                .sortOrder(2)
                .status(MenuStatus.ACTIVE)
                .build());
        menuCount++;

        // 2-3. 페이지 관리
        menuRepository.save(Menu.builder()
                .site(site)
                .parent(content)
                .name("페이지 관리")
                .code("pages")
                .type(MenuType.INTERNAL)
                .url("/pages")
                .icon("mdi-file-document")
                .sortOrder(3)
                .status(MenuStatus.ACTIVE)
                .build());
        menuCount++;

        // 2-4. 미디어 관리
        menuRepository.save(Menu.builder()
                .site(site)
                .parent(content)
                .name("미디어 관리")
                .code("media")
                .type(MenuType.INTERNAL)
                .url("/media")
                .icon("mdi-image-multiple")
                .sortOrder(4)
                .status(MenuStatus.ACTIVE)
                .build());
        menuCount++;

        // 3. 사이트 관리 (디렉토리)
        Menu siteManage = menuRepository.save(Menu.builder()
                .site(site)
                .name("사이트 관리")
                .code("site-manage")
                .type(MenuType.DIRECTORY)
                .icon("mdi-cog")
                .sortOrder(3)
                .status(MenuStatus.ACTIVE)
                .build());
        menuCount++;

        // 3-1. 메뉴 관리
        menuRepository.save(Menu.builder()
                .site(site)
                .parent(siteManage)
                .name("메뉴 관리")
                .code("menus")
                .type(MenuType.INTERNAL)
                .url("/menus")
                .icon("mdi-menu")
                .sortOrder(1)
                .status(MenuStatus.ACTIVE)
                .build());
        menuCount++;

        // 3-2. 사이트 설정
        menuRepository.save(Menu.builder()
                .site(site)
                .parent(siteManage)
                .name("사이트 설정")
                .code("settings")
                .type(MenuType.INTERNAL)
                .url("/settings")
                .icon("mdi-cog-outline")
                .sortOrder(2)
                .status(MenuStatus.ACTIVE)
                .build());
        menuCount++;

        // 4. 사용자 관리 (디렉토리)
        Menu userManage = menuRepository.save(Menu.builder()
                .site(site)
                .name("사용자 관리")
                .code("user-manage")
                .type(MenuType.DIRECTORY)
                .icon("mdi-account-group")
                .sortOrder(4)
                .status(MenuStatus.ACTIVE)
                .roles("SUPER_ADMIN,SITE_ADMIN")
                .build());
        menuCount++;

        // 4-1. 사용자 목록
        menuRepository.save(Menu.builder()
                .site(site)
                .parent(userManage)
                .name("사용자 목록")
                .code("users")
                .type(MenuType.INTERNAL)
                .url("/users")
                .icon("mdi-account")
                .sortOrder(1)
                .status(MenuStatus.ACTIVE)
                .roles("SUPER_ADMIN,SITE_ADMIN")
                .build());
        menuCount++;

        // 4-2. 역할 관리
        menuRepository.save(Menu.builder()
                .site(site)
                .parent(userManage)
                .name("역할 관리")
                .code("roles")
                .type(MenuType.INTERNAL)
                .url("/roles")
                .icon("mdi-shield-account")
                .sortOrder(2)
                .status(MenuStatus.ACTIVE)
                .roles("SUPER_ADMIN")
                .build());
        menuCount++;

        // 5. 시스템 (디렉토리) - 슈퍼 관리자 전용
        Menu system = menuRepository.save(Menu.builder()
                .site(site)
                .name("시스템")
                .code("system")
                .type(MenuType.DIRECTORY)
                .icon("mdi-server")
                .sortOrder(5)
                .status(MenuStatus.ACTIVE)
                .roles("SUPER_ADMIN")
                .build());
        menuCount++;

        // 5-1. 활동 로그
        menuRepository.save(Menu.builder()
                .site(site)
                .parent(system)
                .name("활동 로그")
                .code("activity-logs")
                .type(MenuType.INTERNAL)
                .url("/activity-logs")
                .icon("mdi-history")
                .sortOrder(1)
                .status(MenuStatus.ACTIVE)
                .roles("SUPER_ADMIN")
                .build());
        menuCount++;

        // 5-2. 사이트 관리 (멀티 사이트)
        menuRepository.save(Menu.builder()
                .site(site)
                .parent(system)
                .name("사이트 목록")
                .code("sites")
                .type(MenuType.INTERNAL)
                .url("/sites")
                .icon("mdi-web")
                .sortOrder(2)
                .status(MenuStatus.ACTIVE)
                .roles("SUPER_ADMIN")
                .build());
        menuCount++;

        // 6. 외부 링크 예시 (비활성)
        menuRepository.save(Menu.builder()
                .site(site)
                .name("도움말")
                .code("help")
                .type(MenuType.EXTERNAL)
                .url("https://docs.example.com")
                .icon("mdi-help-circle")
                .sortOrder(99)
                .status(MenuStatus.INACTIVE)
                .target("_blank")
                .build());
        menuCount++;

        return ApiResponse.success("관리자 메뉴 샘플 데이터 생성 완료", Map.of(
                "site", site.getName(),
                "menus", menuCount
        ));
    }
}
