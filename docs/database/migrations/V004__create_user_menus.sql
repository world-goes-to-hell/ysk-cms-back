-- =============================================
-- V004: 사용자 메뉴 테이블 생성
-- =============================================

CREATE TABLE user_menus (
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
    description VARCHAR(500),
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_menus_site_code (site_id, code),
    INDEX idx_user_menu_site (site_id),
    INDEX idx_user_menu_parent (parent_id),
    INDEX idx_user_menu_sort (site_id, parent_id, sort_order),
    CONSTRAINT fk_user_menus_site FOREIGN KEY (site_id) REFERENCES sites(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_menus_parent FOREIGN KEY (parent_id) REFERENCES user_menus(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
