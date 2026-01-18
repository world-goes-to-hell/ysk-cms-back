-- =====================================================
-- Analytics 통계 시스템 DDL
-- 생성일: 2026-01-18
-- =====================================================

-- 1. 페이지뷰 이벤트 테이블 (원본 데이터)
CREATE TABLE analytics_page_view (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    site_code VARCHAR(50) NOT NULL,
    session_id VARCHAR(100) NOT NULL,
    visitor_id VARCHAR(100) NOT NULL,
    user_id BIGINT NULL,

    -- 페이지 정보
    page_url VARCHAR(2000) NOT NULL,
    page_path VARCHAR(500) NOT NULL,
    page_title VARCHAR(500),
    referrer VARCHAR(2000),

    -- 디바이스 정보
    user_agent VARCHAR(1000),
    device_type VARCHAR(20),
    browser VARCHAR(50),
    browser_version VARCHAR(50),
    os VARCHAR(50),
    os_version VARCHAR(50),

    -- 화면 정보
    screen_width INT,
    screen_height INT,

    -- 지역 정보
    ip_address VARCHAR(45),
    country VARCHAR(100),
    city VARCHAR(100),

    -- UTM 파라미터
    utm_source VARCHAR(200),
    utm_medium VARCHAR(200),
    utm_campaign VARCHAR(200),
    utm_term VARCHAR(200),
    utm_content VARCHAR(200),

    -- 시간 정보
    time_on_page INT DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_site_created (site_code, created_at),
    INDEX idx_session (session_id),
    INDEX idx_visitor (visitor_id),
    INDEX idx_page_path (site_code, page_path(255), created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- 2. 세션 테이블
CREATE TABLE analytics_session (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(100) NOT NULL UNIQUE,
    site_code VARCHAR(50) NOT NULL,
    visitor_id VARCHAR(100) NOT NULL,
    user_id BIGINT NULL,

    start_time DATETIME NOT NULL,
    end_time DATETIME,
    page_views INT DEFAULT 1,
    duration INT DEFAULT 0,
    is_bounce TINYINT(1) DEFAULT 1,

    entry_page VARCHAR(500),
    exit_page VARCHAR(500),
    referrer VARCHAR(2000),

    device_type VARCHAR(20),
    browser VARCHAR(50),
    os VARCHAR(50),

    country VARCHAR(100),
    city VARCHAR(100),

    utm_source VARCHAR(200),
    utm_medium VARCHAR(200),
    utm_campaign VARCHAR(200),

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_site_start (site_code, start_time),
    INDEX idx_visitor (visitor_id),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- 3. 일별 통계 테이블
CREATE TABLE analytics_daily_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    site_code VARCHAR(50) NOT NULL,
    stats_date DATE NOT NULL,

    total_visitors INT DEFAULT 0,
    unique_visitors INT DEFAULT 0,
    new_visitors INT DEFAULT 0,
    returning_visitors INT DEFAULT 0,

    total_page_views INT DEFAULT 0,
    avg_page_views DECIMAL(5,2) DEFAULT 0,

    total_sessions INT DEFAULT 0,
    avg_session_duration INT DEFAULT 0,
    bounce_sessions INT DEFAULT 0,
    bounce_rate DECIMAL(5,2) DEFAULT 0,

    hourly_page_views JSON,
    hourly_sessions JSON,

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE INDEX idx_site_date (site_code, stats_date),
    INDEX idx_stats_date (stats_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- 4. 페이지별 통계 테이블
CREATE TABLE analytics_page_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    site_code VARCHAR(50) NOT NULL,
    stats_date DATE NOT NULL,
    page_path VARCHAR(500) NOT NULL,
    page_title VARCHAR(500),

    page_views INT DEFAULT 0,
    unique_page_views INT DEFAULT 0,
    avg_time_on_page INT DEFAULT 0,
    entrances INT DEFAULT 0,
    exits INT DEFAULT 0,
    bounce_rate DECIMAL(5,2) DEFAULT 0,

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE INDEX idx_site_date_page (site_code, stats_date, page_path(255)),
    INDEX idx_stats_date (stats_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- 5. 유입 경로별 통계 테이블
CREATE TABLE analytics_referrer_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    site_code VARCHAR(50) NOT NULL,
    stats_date DATE NOT NULL,

    referrer_type VARCHAR(50) NOT NULL,
    referrer_source VARCHAR(200),

    sessions INT DEFAULT 0,
    page_views INT DEFAULT 0,
    new_visitors INT DEFAULT 0,
    bounce_rate DECIMAL(5,2) DEFAULT 0,
    avg_session_duration INT DEFAULT 0,

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    UNIQUE INDEX idx_site_date_ref (site_code, stats_date, referrer_type, referrer_source(100)),
    INDEX idx_stats_date (stats_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- 6. 디바이스별 통계 테이블
CREATE TABLE analytics_device_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    site_code VARCHAR(50) NOT NULL,
    stats_date DATE NOT NULL,

    device_type VARCHAR(20) NOT NULL,
    browser VARCHAR(50),
    os VARCHAR(50),

    sessions INT DEFAULT 0,
    unique_visitors INT DEFAULT 0,
    page_views INT DEFAULT 0,
    bounce_rate DECIMAL(5,2) DEFAULT 0,
    avg_session_duration INT DEFAULT 0,

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    UNIQUE INDEX idx_site_date_device (site_code, stats_date, device_type, browser(50), os(50)),
    INDEX idx_stats_date (stats_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- 7. 방문자 테이블
CREATE TABLE analytics_visitor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    visitor_id VARCHAR(100) NOT NULL,
    site_code VARCHAR(50) NOT NULL,

    first_visit_at DATETIME NOT NULL,
    last_visit_at DATETIME NOT NULL,
    total_visits INT DEFAULT 1,
    total_page_views INT DEFAULT 1,

    first_referrer VARCHAR(2000),
    first_utm_source VARCHAR(200),
    first_utm_medium VARCHAR(200),
    first_utm_campaign VARCHAR(200),

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE INDEX idx_site_visitor (site_code, visitor_id),
    INDEX idx_last_visit (last_visit_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
