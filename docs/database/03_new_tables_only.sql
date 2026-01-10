-- =============================================
-- YSK CMS - 신규 테이블만 (기존 5개 테이블 제외)
-- 기존 테이블: users, roles, permissions, user_roles, role_permissions
-- =============================================

-- 1. 사이트 테이블
CREATE TABLE sites (
    id BIGINT NOT NULL AUTO_INCREMENT,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    domain VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    settings JSON,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    PRIMARY KEY (id),
    UNIQUE KEY uk_sites_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. 사용자-사이트 연결 테이블
CREATE TABLE user_sites (
    user_id BIGINT NOT NULL,
    site_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, site_id),
    CONSTRAINT fk_user_sites_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_sites_site FOREIGN KEY (site_id) REFERENCES sites(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. 게시판 테이블
CREATE TABLE boards (
    id BIGINT NOT NULL AUTO_INCREMENT,
    site_id BIGINT NOT NULL,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    type VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    use_comment TINYINT(1) NOT NULL DEFAULT 0,
    use_attachment TINYINT(1) NOT NULL DEFAULT 1,
    attachment_limit INT NOT NULL DEFAULT 5,
    sort_order INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    PRIMARY KEY (id),
    UNIQUE KEY uk_boards_site_code (site_id, code),
    CONSTRAINT fk_boards_site FOREIGN KEY (site_id) REFERENCES sites(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. 게시글 테이블
CREATE TABLE posts (
    id BIGINT NOT NULL AUTO_INCREMENT,
    board_id BIGINT NOT NULL,
    title VARCHAR(300) NOT NULL,
    content LONGTEXT,
    author VARCHAR(50),
    view_count INT NOT NULL DEFAULT 0,
    is_pinned TINYINT(1) NOT NULL DEFAULT 0,
    is_secret TINYINT(1) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    published_at DATETIME(6),
    answer TEXT,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    PRIMARY KEY (id),
    INDEX idx_post_board_created (board_id, created_at DESC),
    CONSTRAINT fk_posts_board FOREIGN KEY (board_id) REFERENCES boards(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. 페이지 테이블
CREATE TABLE pages (
    id BIGINT NOT NULL AUTO_INCREMENT,
    site_id BIGINT NOT NULL,
    parent_id BIGINT,
    slug VARCHAR(100) NOT NULL,
    title VARCHAR(200) NOT NULL,
    content LONGTEXT,
    meta_description VARCHAR(300),
    meta_keywords VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    published_at DATETIME(6),
    sort_order INT NOT NULL DEFAULT 0,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    PRIMARY KEY (id),
    UNIQUE KEY uk_pages_site_slug (site_id, slug),
    INDEX idx_pages_parent (parent_id),
    CONSTRAINT fk_pages_site FOREIGN KEY (site_id) REFERENCES sites(id) ON DELETE CASCADE,
    CONSTRAINT fk_pages_parent FOREIGN KEY (parent_id) REFERENCES pages(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. 미디어 테이블
CREATE TABLE media (
    id BIGINT NOT NULL AUTO_INCREMENT,
    site_id BIGINT,
    original_name VARCHAR(255) NOT NULL,
    stored_name VARCHAR(500) NOT NULL,
    file_path VARCHAR(1000) NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    description VARCHAR(500),
    alt_text VARCHAR(500),
    width INT,
    height INT,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    PRIMARY KEY (id),
    INDEX idx_media_site (site_id),
    INDEX idx_media_type (type),
    CONSTRAINT fk_media_site FOREIGN KEY (site_id) REFERENCES sites(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. 활동 로그 테이블
CREATE TABLE activity_logs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT,
    site_id BIGINT,
    activity_type VARCHAR(20) NOT NULL,
    target_type VARCHAR(50) NOT NULL,
    target_id BIGINT,
    target_name VARCHAR(200),
    description VARCHAR(500),
    ip_address VARCHAR(50),
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_activity_created (created_at DESC),
    INDEX idx_activity_user (user_id),
    INDEX idx_activity_site (site_id),
    CONSTRAINT fk_activity_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_activity_site FOREIGN KEY (site_id) REFERENCES sites(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. 관리자 메뉴 테이블
CREATE TABLE admin_menus (
    id BIGINT NOT NULL AUTO_INCREMENT,
    site_id BIGINT NOT NULL,
    parent_id BIGINT,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50),
    type VARCHAR(20) NOT NULL DEFAULT 'INTERNAL',
    url VARCHAR(500),
    icon VARCHAR(50),
    sort_order INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    target VARCHAR(20) DEFAULT '_self',
    roles VARCHAR(500),
    description VARCHAR(500),
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    PRIMARY KEY (id),
    UNIQUE KEY uk_admin_menus_site_code (site_id, code),
    INDEX idx_admin_menu_site (site_id),
    INDEX idx_admin_menu_parent (parent_id),
    INDEX idx_admin_menu_sort (site_id, parent_id, sort_order),
    CONSTRAINT fk_admin_menus_site FOREIGN KEY (site_id) REFERENCES sites(id) ON DELETE CASCADE,
    CONSTRAINT fk_admin_menus_parent FOREIGN KEY (parent_id) REFERENCES admin_menus(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
