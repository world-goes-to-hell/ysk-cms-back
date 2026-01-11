-- =====================================================
-- 07. boards 테이블 옵션 컬럼 추가 마이그레이션
-- use_secret: 비밀글 기능 사용 여부
-- use_pinned: 상단 고정 기능 사용 여부
-- =====================================================

-- 비밀글 기능 사용 여부 컬럼 추가
ALTER TABLE boards
ADD COLUMN use_secret TINYINT(1) NOT NULL DEFAULT 0
AFTER attachment_limit;

-- 상단 고정 기능 사용 여부 컬럼 추가
ALTER TABLE boards
ADD COLUMN use_pinned TINYINT(1) NOT NULL DEFAULT 0
AFTER use_secret;

-- 확인
-- SELECT id, code, name, use_comment, use_attachment, attachment_limit, use_secret, use_pinned FROM boards;
