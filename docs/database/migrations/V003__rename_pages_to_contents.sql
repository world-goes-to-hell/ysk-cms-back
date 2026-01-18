-- =============================================
-- Migration: pages -> contents 테이블명 변경
-- Date: 2025-01-18
-- Description: 테이블명을 더 명확한 'contents'로 변경
-- =============================================

-- 1. 외래키 제약조건 제거
ALTER TABLE pages DROP FOREIGN KEY fk_pages_site;
ALTER TABLE pages DROP FOREIGN KEY fk_pages_parent;

-- 2. 인덱스 및 유니크 키 제거
ALTER TABLE pages DROP INDEX uk_pages_site_slug;
ALTER TABLE pages DROP INDEX idx_pages_parent;

-- 3. 테이블명 변경
RENAME TABLE pages TO contents;

-- 4. 새로운 제약조건 및 인덱스 추가
ALTER TABLE contents
    ADD UNIQUE KEY uk_contents_site_slug (site_id, slug),
    ADD INDEX idx_contents_parent (parent_id),
    ADD CONSTRAINT fk_contents_site FOREIGN KEY (site_id) REFERENCES sites(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_contents_parent FOREIGN KEY (parent_id) REFERENCES contents(id) ON DELETE SET NULL;
