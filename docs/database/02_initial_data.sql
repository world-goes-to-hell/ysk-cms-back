-- =============================================
-- YSK CMS Initial Data
-- =============================================

-- =============================================
-- 1. 역할 초기 데이터
-- =============================================

INSERT INTO roles (id, name, description, created_at, updated_at) VALUES
(1, 'SUPER_ADMIN', '슈퍼 관리자 - 전체 시스템 관리 권한', NOW(), NOW()),
(2, 'SITE_ADMIN', '사이트 관리자 - 할당된 사이트 관리 권한', NOW(), NOW()),
(3, 'EDITOR', '에디터 - 콘텐츠 작성/수정 권한', NOW(), NOW()),
(4, 'VIEWER', '뷰어 - 읽기 전용 권한', NOW(), NOW());

-- =============================================
-- 2. 권한 초기 데이터
-- =============================================

-- USER 권한
INSERT INTO permissions (id, name, description, resource_type, action_type, created_at, updated_at) VALUES
(1, 'USER_CREATE', '사용자 생성', 'USER', 'CREATE', NOW(), NOW()),
(2, 'USER_READ', '사용자 조회', 'USER', 'READ', NOW(), NOW()),
(3, 'USER_UPDATE', '사용자 수정', 'USER', 'UPDATE', NOW(), NOW()),
(4, 'USER_DELETE', '사용자 삭제', 'USER', 'DELETE', NOW(), NOW());

-- SITE 권한
INSERT INTO permissions (id, name, description, resource_type, action_type, created_at, updated_at) VALUES
(5, 'SITE_CREATE', '사이트 생성', 'SITE', 'CREATE', NOW(), NOW()),
(6, 'SITE_READ', '사이트 조회', 'SITE', 'READ', NOW(), NOW()),
(7, 'SITE_UPDATE', '사이트 수정', 'SITE', 'UPDATE', NOW(), NOW()),
(8, 'SITE_DELETE', '사이트 삭제', 'SITE', 'DELETE', NOW(), NOW());

-- BOARD 권한
INSERT INTO permissions (id, name, description, resource_type, action_type, created_at, updated_at) VALUES
(9, 'BOARD_CREATE', '게시판 생성', 'BOARD', 'CREATE', NOW(), NOW()),
(10, 'BOARD_READ', '게시판 조회', 'BOARD', 'READ', NOW(), NOW()),
(11, 'BOARD_UPDATE', '게시판 수정', 'BOARD', 'UPDATE', NOW(), NOW()),
(12, 'BOARD_DELETE', '게시판 삭제', 'BOARD', 'DELETE', NOW(), NOW());

-- POST 권한
INSERT INTO permissions (id, name, description, resource_type, action_type, created_at, updated_at) VALUES
(13, 'POST_CREATE', '게시글 작성', 'POST', 'CREATE', NOW(), NOW()),
(14, 'POST_READ', '게시글 조회', 'POST', 'READ', NOW(), NOW()),
(15, 'POST_UPDATE', '게시글 수정', 'POST', 'UPDATE', NOW(), NOW()),
(16, 'POST_DELETE', '게시글 삭제', 'POST', 'DELETE', NOW(), NOW());

-- PAGE 권한
INSERT INTO permissions (id, name, description, resource_type, action_type, created_at, updated_at) VALUES
(17, 'PAGE_CREATE', '페이지 생성', 'PAGE', 'CREATE', NOW(), NOW()),
(18, 'PAGE_READ', '페이지 조회', 'PAGE', 'READ', NOW(), NOW()),
(19, 'PAGE_UPDATE', '페이지 수정', 'PAGE', 'UPDATE', NOW(), NOW()),
(20, 'PAGE_DELETE', '페이지 삭제', 'PAGE', 'DELETE', NOW(), NOW());

-- MEDIA 권한
INSERT INTO permissions (id, name, description, resource_type, action_type, created_at, updated_at) VALUES
(21, 'MEDIA_CREATE', '미디어 업로드', 'MEDIA', 'CREATE', NOW(), NOW()),
(22, 'MEDIA_READ', '미디어 조회', 'MEDIA', 'READ', NOW(), NOW()),
(23, 'MEDIA_UPDATE', '미디어 수정', 'MEDIA', 'UPDATE', NOW(), NOW()),
(24, 'MEDIA_DELETE', '미디어 삭제', 'MEDIA', 'DELETE', NOW(), NOW());

-- =============================================
-- 3. 역할-권한 매핑
-- =============================================

-- SUPER_ADMIN: 모든 권한
INSERT INTO role_permissions (role_id, permission_id)
SELECT 1, id FROM permissions;

-- SITE_ADMIN: 사이트 관리 + 콘텐츠 전체 권한
INSERT INTO role_permissions (role_id, permission_id) VALUES
(2, 6), (2, 7),  -- SITE READ, UPDATE
(2, 9), (2, 10), (2, 11), (2, 12),  -- BOARD 전체
(2, 13), (2, 14), (2, 15), (2, 16),  -- POST 전체
(2, 17), (2, 18), (2, 19), (2, 20),  -- PAGE 전체
(2, 21), (2, 22), (2, 23), (2, 24);  -- MEDIA 전체

-- EDITOR: 콘텐츠 작성/수정 권한
INSERT INTO role_permissions (role_id, permission_id) VALUES
(3, 6),  -- SITE READ
(3, 10),  -- BOARD READ
(3, 13), (3, 14), (3, 15),  -- POST CREATE, READ, UPDATE
(3, 17), (3, 18), (3, 19),  -- PAGE CREATE, READ, UPDATE
(3, 21), (3, 22), (3, 23);  -- MEDIA CREATE, READ, UPDATE

-- VIEWER: 읽기 전용
INSERT INTO role_permissions (role_id, permission_id) VALUES
(4, 6),   -- SITE READ
(4, 10),  -- BOARD READ
(4, 14),  -- POST READ
(4, 18),  -- PAGE READ
(4, 22);  -- MEDIA READ

-- =============================================
-- 4. 기본 관리자 계정 (비밀번호: admin123)
-- BCrypt 해시: $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH
-- =============================================

INSERT INTO users (id, username, password, email, name, status, created_at, updated_at) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKh0CYie', 'admin@ysk.com', '시스템관리자', 'ACTIVE', NOW(), NOW());

INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);

-- =============================================
-- 5. 기본 사이트 데이터
-- =============================================

INSERT INTO sites (id, code, name, description, domain, status, created_at, updated_at) VALUES
(1, 'main', '메인 사이트', 'YSK CMS 메인 사이트', 'localhost', 'ACTIVE', NOW(), NOW());

-- 관리자에게 메인 사이트 접근 권한 부여
INSERT INTO user_sites (user_id, site_id) VALUES (1, 1);

-- =============================================
-- 6. 샘플 게시판 데이터
-- =============================================

INSERT INTO boards (id, site_id, code, name, description, type, use_comment, use_attachment, sort_order, status, created_at, updated_at) VALUES
(1, 1, 'notice', '공지사항', '시스템 공지사항 게시판', 'NOTICE', 0, 1, 1, 'ACTIVE', NOW(), NOW()),
(2, 1, 'faq', 'FAQ', '자주 묻는 질문', 'FAQ', 0, 0, 2, 'ACTIVE', NOW(), NOW()),
(3, 1, 'qna', 'Q&A', '질문과 답변', 'QNA', 0, 1, 3, 'ACTIVE', NOW(), NOW()),
(4, 1, 'gallery', '갤러리', '이미지 갤러리', 'GALLERY', 1, 1, 4, 'ACTIVE', NOW(), NOW()),
(5, 1, 'free', '자유게시판', '자유롭게 글을 작성하세요', 'NORMAL', 1, 1, 5, 'ACTIVE', NOW(), NOW());
