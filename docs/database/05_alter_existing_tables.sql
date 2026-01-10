-- =============================================
-- YSK CMS - 모든 테이블에 BaseEntity 컬럼 추가
-- 필요한 컬럼: deleted, created_by, updated_by
-- =============================================

-- 1. users 테이블
ALTER TABLE users
ADD COLUMN IF NOT EXISTS deleted TINYINT(1) NOT NULL DEFAULT 0,
ADD COLUMN IF NOT EXISTS created_by VARCHAR(255),
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);

-- 2. roles 테이블
ALTER TABLE roles
ADD COLUMN IF NOT EXISTS deleted TINYINT(1) NOT NULL DEFAULT 0,
ADD COLUMN IF NOT EXISTS created_by VARCHAR(255),
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);

-- 3. permissions 테이블
ALTER TABLE permissions
ADD COLUMN IF NOT EXISTS deleted TINYINT(1) NOT NULL DEFAULT 0,
ADD COLUMN IF NOT EXISTS created_by VARCHAR(255),
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);

-- 4. sites 테이블
ALTER TABLE sites
ADD COLUMN IF NOT EXISTS deleted TINYINT(1) NOT NULL DEFAULT 0,
ADD COLUMN IF NOT EXISTS created_by VARCHAR(255),
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);

-- 5. boards 테이블
ALTER TABLE boards
ADD COLUMN IF NOT EXISTS deleted TINYINT(1) NOT NULL DEFAULT 0,
ADD COLUMN IF NOT EXISTS created_by VARCHAR(255),
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);

-- 6. posts 테이블
ALTER TABLE posts
ADD COLUMN IF NOT EXISTS deleted TINYINT(1) NOT NULL DEFAULT 0,
ADD COLUMN IF NOT EXISTS created_by VARCHAR(255),
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);

-- 7. pages 테이블
ALTER TABLE pages
ADD COLUMN IF NOT EXISTS deleted TINYINT(1) NOT NULL DEFAULT 0,
ADD COLUMN IF NOT EXISTS created_by VARCHAR(255),
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);

-- 8. media 테이블
ALTER TABLE media
ADD COLUMN IF NOT EXISTS deleted TINYINT(1) NOT NULL DEFAULT 0,
ADD COLUMN IF NOT EXISTS created_by VARCHAR(255),
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);

-- =============================================
-- 확인용 쿼리
-- =============================================
-- DESCRIBE posts;
-- DESCRIBE boards;
-- DESCRIBE sites;
