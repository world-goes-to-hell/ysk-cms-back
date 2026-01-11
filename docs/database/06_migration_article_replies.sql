-- =============================================
-- 게시글 댓글 테이블 생성
-- Database: MariaDB 11.2
-- Created: 2024-01-11
-- =============================================

-- 게시글 댓글 테이블
CREATE TABLE board_article_replies (
    id BIGINT NOT NULL AUTO_INCREMENT,
    article_id BIGINT NOT NULL,
    parent_id BIGINT,
    content TEXT NOT NULL,
    author VARCHAR(50),
    is_secret TINYINT(1) NOT NULL DEFAULT 0,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    PRIMARY KEY (id),
    INDEX idx_reply_article (article_id),
    INDEX idx_reply_parent (parent_id),
    INDEX idx_reply_created (article_id, created_at DESC),
    CONSTRAINT fk_replies_article FOREIGN KEY (article_id) REFERENCES board_articles(id) ON DELETE CASCADE,
    CONSTRAINT fk_replies_parent FOREIGN KEY (parent_id) REFERENCES board_article_replies(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 게시글 테이블에 댓글 수 컬럼 추가 (선택사항 - 성능 최적화용)
ALTER TABLE board_articles ADD COLUMN reply_count INT NOT NULL DEFAULT 0 AFTER view_count;
