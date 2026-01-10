-- =============================================
-- YSK CMS - 관리자 메뉴 테이블 생성
-- 실행 전 sites 테이블이 먼저 생성되어 있어야 합니다.
-- =============================================

CREATE TABLE IF NOT EXISTS admin_menus (
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

-- =============================================
-- 메뉴 타입 (MenuType enum)
-- =============================================
-- DIRECTORY  : 디렉토리 (하위 메뉴를 가지는 폴더)
-- INTERNAL   : 내부 링크 (사이트 내부 페이지)
-- EXTERNAL   : 외부 링크 (외부 사이트)
-- BOARD      : 게시판
-- PAGE       : 정적 페이지

-- =============================================
-- 메뉴 상태 (MenuStatus enum)
-- =============================================
-- ACTIVE     : 활성
-- INACTIVE   : 비활성
