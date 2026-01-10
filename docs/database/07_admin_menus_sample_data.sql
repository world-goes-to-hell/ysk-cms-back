-- =============================================
-- YSK CMS - 관리자 메뉴 샘플 데이터
-- 실행 전 sites 테이블에 'main' 사이트가 있어야 합니다.
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

-- =============================================
-- 확인용 쿼리
-- =============================================
-- SELECT * FROM admin_menus ORDER BY sort_order, id;
-- SELECT m.*, p.name as parent_name FROM admin_menus m LEFT JOIN admin_menus p ON m.parent_id = p.id;
