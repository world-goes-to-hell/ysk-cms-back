-- =============================================
-- YSK CMS Sample Data (테스트용)
-- 02_initial_data.sql 실행 후 사용
-- =============================================

-- =============================================
-- 1. 추가 사이트 데이터
-- =============================================

INSERT INTO sites (id, code, name, description, domain, status, created_at, updated_at) VALUES
(2, 'blog', '블로그', '공식 블로그 사이트', 'blog.ysk.com', 'ACTIVE', NOW(), NOW()),
(3, 'shop', '쇼핑몰', '온라인 쇼핑몰', 'shop.ysk.com', 'ACTIVE', NOW(), NOW());

-- =============================================
-- 2. 샘플 게시글 데이터
-- =============================================

-- 공지사항
INSERT INTO posts (board_id, title, content, author, view_count, is_pinned, status, published_at, created_at, updated_at) VALUES
(1, '2024년 시스템 업데이트 안내', '<p>안녕하세요. 2024년 시스템 업데이트 내용을 안내드립니다.</p>', '관리자', 342, 1, 'PUBLISHED', NOW(), NOW(), NOW()),
(1, '신규 기능 사용 가이드', '<p>새롭게 추가된 기능들에 대한 사용 가이드입니다.</p>', '관리자', 256, 0, 'PUBLISHED', NOW(), NOW(), NOW()),
(1, '서버 점검 일정 안내', '<p>1월 15일 서버 점검이 예정되어 있습니다.</p>', '운영팀', 189, 0, 'PUBLISHED', NOW(), NOW(), NOW());

-- FAQ
INSERT INTO posts (board_id, title, content, author, view_count, status, answer, published_at, created_at, updated_at) VALUES
(2, '비밀번호를 잊어버렸어요', '<p>비밀번호를 잊어버린 경우 어떻게 해야 하나요?</p>', '관리자', 521, 'PUBLISHED', '<p>로그인 페이지에서 "비밀번호 찾기"를 클릭하세요.</p>', NOW(), NOW(), NOW()),
(2, '회원 탈퇴는 어떻게 하나요?', '<p>회원 탈퇴 절차를 알려주세요.</p>', '관리자', 312, 'PUBLISHED', '<p>마이페이지 > 회원정보 > 회원탈퇴에서 진행하실 수 있습니다.</p>', NOW(), NOW(), NOW());

-- Q&A
INSERT INTO posts (board_id, title, content, author, view_count, is_secret, status, published_at, created_at, updated_at) VALUES
(3, '배송 관련 문의드립니다', '<p>주문한 상품의 배송이 늦어지고 있습니다.</p>', '홍길동', 45, 0, 'PUBLISHED', NOW(), NOW(), NOW()),
(3, '환불 요청합니다', '<p>상품에 하자가 있어 환불을 요청합니다.</p>', '김철수', 32, 1, 'PUBLISHED', NOW(), NOW(), NOW());

-- 자유게시판
INSERT INTO posts (board_id, title, content, author, view_count, status, published_at, created_at, updated_at) VALUES
(5, '오늘 날씨가 좋네요', '<p>봄이 오는 것 같습니다.</p>', '날씨요정', 892, 'PUBLISHED', NOW(), NOW(), NOW()),
(5, '맛집 추천해주세요', '<p>강남역 근처 맛집 추천 부탁드립니다.</p>', '먹보', 156, 'PUBLISHED', NOW(), NOW(), NOW());

-- =============================================
-- 3. 샘플 페이지 데이터
-- =============================================

INSERT INTO pages (site_id, slug, title, content, meta_description, status, sort_order, published_at, created_at, updated_at) VALUES
(1, 'about', '회사소개', '<h1>YSK CMS</h1><p>최고의 콘텐츠 관리 시스템입니다.</p>', 'YSK CMS 회사 소개 페이지', 'PUBLISHED', 1, NOW(), NOW(), NOW()),
(1, 'terms', '이용약관', '<h1>이용약관</h1><p>서비스 이용약관 내용입니다.</p>', '서비스 이용약관', 'PUBLISHED', 2, NOW(), NOW(), NOW()),
(1, 'privacy', '개인정보처리방침', '<h1>개인정보처리방침</h1><p>개인정보 처리에 관한 내용입니다.</p>', '개인정보 처리방침', 'PUBLISHED', 3, NOW(), NOW(), NOW()),
(1, 'contact', '문의하기', '<h1>문의하기</h1><p>이메일: contact@ysk.com</p>', '고객센터 문의', 'PUBLISHED', 4, NOW(), NOW(), NOW());

-- 하위 페이지 예시
INSERT INTO pages (site_id, parent_id, slug, title, content, status, sort_order, published_at, created_at, updated_at) VALUES
(1, 1, 'about/history', '연혁', '<h2>회사 연혁</h2><p>2020년 설립...</p>', 'PUBLISHED', 1, NOW(), NOW(), NOW()),
(1, 1, 'about/team', '팀 소개', '<h2>우리 팀을 소개합니다</h2>', 'PUBLISHED', 2, NOW(), NOW(), NOW());

-- =============================================
-- 4. 샘플 활동 로그 데이터
-- =============================================

INSERT INTO activity_logs (user_id, site_id, activity_type, target_type, target_id, target_name, description, ip_address, created_at) VALUES
(1, 1, 'LOGIN', 'USER', 1, 'admin', '관리자 로그인', '127.0.0.1', DATE_SUB(NOW(), INTERVAL 5 MINUTE)),
(1, 1, 'CREATE', 'POST', 1, '2024년 시스템 업데이트 안내', '공지사항 게시글 작성', '127.0.0.1', DATE_SUB(NOW(), INTERVAL 12 MINUTE)),
(1, 1, 'UPDATE', 'USER', 2, '홍길동', '사용자 정보 수정', '127.0.0.1', DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(1, 1, 'DELETE', 'POST', 99, '테스트 게시글', '게시글 삭제', '127.0.0.1', DATE_SUB(NOW(), INTERVAL 2 HOUR));

-- =============================================
-- 5. 추가 사용자 데이터
-- =============================================

-- 비밀번호: test123 (BCrypt)
INSERT INTO users (id, username, password, email, name, status, created_at, updated_at) VALUES
(2, 'siteadmin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKh0CYie', 'siteadmin@ysk.com', '사이트관리자', 'ACTIVE', NOW(), NOW()),
(3, 'editor', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKh0CYie', 'editor@ysk.com', '에디터', 'ACTIVE', NOW(), NOW()),
(4, 'viewer', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKh0CYie', 'viewer@ysk.com', '뷰어', 'ACTIVE', NOW(), NOW());

-- 역할 할당
INSERT INTO user_roles (user_id, role_id) VALUES
(2, 2),  -- siteadmin: SITE_ADMIN
(3, 3),  -- editor: EDITOR
(4, 4);  -- viewer: VIEWER

-- 사이트 접근 권한
INSERT INTO user_sites (user_id, site_id) VALUES
(2, 1), (2, 2),  -- siteadmin: main, blog
(3, 1),          -- editor: main
(4, 1);          -- viewer: main

-- =============================================
-- 6. 관리자 메뉴 샘플 데이터
-- =============================================

-- 사이트 ID 조회 (main 사이트)
SET @site_id = (SELECT id FROM sites WHERE code = 'main');

-- 1. 대시보드
INSERT INTO admin_menus (site_id, parent_id, name, code, type, url, icon, sort_order, status, target, roles, description, deleted, created_at, updated_at)
VALUES (@site_id, NULL, '대시보드', 'dashboard', 'INTERNAL', '/dashboard', 'mdi-view-dashboard', 1, 'ACTIVE', '_self', NULL, NULL, 0, NOW(), NOW());

-- 2. 콘텐츠 관리 (디렉토리)
INSERT INTO admin_menus (site_id, parent_id, name, code, type, url, icon, sort_order, status, target, roles, description, deleted, created_at, updated_at)
VALUES (@site_id, NULL, '콘텐츠 관리', 'content', 'DIRECTORY', NULL, 'mdi-file-document-multiple', 2, 'ACTIVE', '_self', NULL, NULL, 0, NOW(), NOW());

SET @content_id = LAST_INSERT_ID();

-- 2-1. 게시판 관리
INSERT INTO admin_menus (site_id, parent_id, name, code, type, url, icon, sort_order, status, target, roles, description, deleted, created_at, updated_at)
VALUES (@site_id, @content_id, '게시판 관리', 'boards', 'INTERNAL', '/boards', 'mdi-bulletin-board', 1, 'ACTIVE', '_self', NULL, NULL, 0, NOW(), NOW());

-- 2-2. 게시글 관리
INSERT INTO admin_menus (site_id, parent_id, name, code, type, url, icon, sort_order, status, target, roles, description, deleted, created_at, updated_at)
VALUES (@site_id, @content_id, '게시글 관리', 'posts', 'INTERNAL', '/posts', 'mdi-post', 2, 'ACTIVE', '_self', NULL, NULL, 0, NOW(), NOW());

-- 2-3. 페이지 관리
INSERT INTO admin_menus (site_id, parent_id, name, code, type, url, icon, sort_order, status, target, roles, description, deleted, created_at, updated_at)
VALUES (@site_id, @content_id, '페이지 관리', 'pages', 'INTERNAL', '/pages', 'mdi-file-document', 3, 'ACTIVE', '_self', NULL, NULL, 0, NOW(), NOW());

-- 2-4. 미디어 관리
INSERT INTO admin_menus (site_id, parent_id, name, code, type, url, icon, sort_order, status, target, roles, description, deleted, created_at, updated_at)
VALUES (@site_id, @content_id, '미디어 관리', 'media', 'INTERNAL', '/media', 'mdi-image-multiple', 4, 'ACTIVE', '_self', NULL, NULL, 0, NOW(), NOW());

-- 3. 사이트 관리 (디렉토리)
INSERT INTO admin_menus (site_id, parent_id, name, code, type, url, icon, sort_order, status, target, roles, description, deleted, created_at, updated_at)
VALUES (@site_id, NULL, '사이트 관리', 'site-manage', 'DIRECTORY', NULL, 'mdi-cog', 3, 'ACTIVE', '_self', NULL, NULL, 0, NOW(), NOW());

SET @site_manage_id = LAST_INSERT_ID();

-- 3-1. 메뉴 관리
INSERT INTO admin_menus (site_id, parent_id, name, code, type, url, icon, sort_order, status, target, roles, description, deleted, created_at, updated_at)
VALUES (@site_id, @site_manage_id, '메뉴 관리', 'menus', 'INTERNAL', '/menus', 'mdi-menu', 1, 'ACTIVE', '_self', NULL, NULL, 0, NOW(), NOW());

-- 3-2. 사이트 설정
INSERT INTO admin_menus (site_id, parent_id, name, code, type, url, icon, sort_order, status, target, roles, description, deleted, created_at, updated_at)
VALUES (@site_id, @site_manage_id, '사이트 설정', 'settings', 'INTERNAL', '/settings', 'mdi-cog-outline', 2, 'ACTIVE', '_self', NULL, NULL, 0, NOW(), NOW());

-- 4. 사용자 관리 (디렉토리)
INSERT INTO admin_menus (site_id, parent_id, name, code, type, url, icon, sort_order, status, target, roles, description, deleted, created_at, updated_at)
VALUES (@site_id, NULL, '사용자 관리', 'user-manage', 'DIRECTORY', NULL, 'mdi-account-group', 4, 'ACTIVE', '_self', 'SUPER_ADMIN,SITE_ADMIN', NULL, 0, NOW(), NOW());

SET @user_manage_id = LAST_INSERT_ID();

-- 4-1. 사용자 목록
INSERT INTO admin_menus (site_id, parent_id, name, code, type, url, icon, sort_order, status, target, roles, description, deleted, created_at, updated_at)
VALUES (@site_id, @user_manage_id, '사용자 목록', 'users', 'INTERNAL', '/users', 'mdi-account', 1, 'ACTIVE', '_self', 'SUPER_ADMIN,SITE_ADMIN', NULL, 0, NOW(), NOW());

-- 4-2. 역할 관리
INSERT INTO admin_menus (site_id, parent_id, name, code, type, url, icon, sort_order, status, target, roles, description, deleted, created_at, updated_at)
VALUES (@site_id, @user_manage_id, '역할 관리', 'roles', 'INTERNAL', '/roles', 'mdi-shield-account', 2, 'ACTIVE', '_self', 'SUPER_ADMIN', NULL, 0, NOW(), NOW());

-- 5. 시스템 (디렉토리)
INSERT INTO admin_menus (site_id, parent_id, name, code, type, url, icon, sort_order, status, target, roles, description, deleted, created_at, updated_at)
VALUES (@site_id, NULL, '시스템', 'system', 'DIRECTORY', NULL, 'mdi-server', 5, 'ACTIVE', '_self', 'SUPER_ADMIN', NULL, 0, NOW(), NOW());

SET @system_id = LAST_INSERT_ID();

-- 5-1. 활동 로그
INSERT INTO admin_menus (site_id, parent_id, name, code, type, url, icon, sort_order, status, target, roles, description, deleted, created_at, updated_at)
VALUES (@site_id, @system_id, '활동 로그', 'activity-logs', 'INTERNAL', '/activity-logs', 'mdi-history', 1, 'ACTIVE', '_self', 'SUPER_ADMIN', NULL, 0, NOW(), NOW());

-- 5-2. 사이트 목록
INSERT INTO admin_menus (site_id, parent_id, name, code, type, url, icon, sort_order, status, target, roles, description, deleted, created_at, updated_at)
VALUES (@site_id, @system_id, '사이트 목록', 'sites', 'INTERNAL', '/sites', 'mdi-web', 2, 'ACTIVE', '_self', 'SUPER_ADMIN', NULL, 0, NOW(), NOW());

-- 6. 도움말 (외부링크, 비활성)
INSERT INTO admin_menus (site_id, parent_id, name, code, type, url, icon, sort_order, status, target, roles, description, deleted, created_at, updated_at)
VALUES (@site_id, NULL, '도움말', 'help', 'EXTERNAL', 'https://docs.example.com', 'mdi-help-circle', 99, 'INACTIVE', '_blank', NULL, NULL, 0, NOW(), NOW());

