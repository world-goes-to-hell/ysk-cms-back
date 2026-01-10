-- =============================================
-- YSK CMS 테이블명 변경 마이그레이션
-- posts → board_articles
-- media → atch_files
-- =============================================

-- 1. posts 테이블을 board_articles로 변경
-- =============================================

-- 1.1 기존 인덱스 삭제
ALTER TABLE posts DROP INDEX idx_post_board_created;
ALTER TABLE posts DROP FOREIGN KEY fk_posts_board;

-- 1.2 테이블명 변경
RENAME TABLE posts TO board_articles;

-- 1.3 새 컬럼 추가 (soft delete 및 audit)
ALTER TABLE board_articles
    ADD COLUMN deleted TINYINT(1) NOT NULL DEFAULT 0 AFTER answer,
    ADD COLUMN created_by VARCHAR(255) AFTER updated_at,
    ADD COLUMN updated_by VARCHAR(255) AFTER created_by;

-- 1.4 새 인덱스 및 FK 생성
ALTER TABLE board_articles
    ADD INDEX idx_article_board_created (board_id, created_at DESC),
    ADD CONSTRAINT fk_board_articles_board FOREIGN KEY (board_id) REFERENCES boards(id) ON DELETE CASCADE;

-- =============================================
-- 2. media 테이블을 atch_files로 변경
-- =============================================

-- 2.1 기존 인덱스 삭제
ALTER TABLE media DROP INDEX idx_media_site;
ALTER TABLE media DROP INDEX idx_media_type;
ALTER TABLE media DROP FOREIGN KEY fk_media_site;

-- 2.2 테이블명 변경
RENAME TABLE media TO atch_files;

-- 2.3 새 컬럼 추가 (soft delete 및 audit)
ALTER TABLE atch_files
    ADD COLUMN deleted TINYINT(1) NOT NULL DEFAULT 0 AFTER height,
    ADD COLUMN created_by VARCHAR(255) AFTER updated_at,
    ADD COLUMN updated_by VARCHAR(255) AFTER created_by;

-- 2.4 새 인덱스 및 FK 생성
ALTER TABLE atch_files
    ADD INDEX idx_atch_file_site (site_id),
    ADD INDEX idx_atch_file_type (type),
    ADD CONSTRAINT fk_atch_files_site FOREIGN KEY (site_id) REFERENCES sites(id) ON DELETE SET NULL;

-- =============================================
-- 마이그레이션 완료 확인
-- =============================================
SELECT 'Migration completed successfully' AS status;
SHOW TABLES LIKE '%article%';
SHOW TABLES LIKE '%atch%';
