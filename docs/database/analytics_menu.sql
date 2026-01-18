-- Analytics 메뉴 추가
-- 실행 전 site_id와 parent_id를 확인해주세요

-- 1. 현재 사이트 ID 확인
-- SELECT id, code, name FROM sites;

-- 2. 현재 메뉴 목록 확인 (parent_id 참조용)
-- SELECT id, name, url FROM admin_menus WHERE site_id = 1;

-- Analytics 메뉴 INSERT (대시보드와 같은 레벨에 추가)
-- site_id와 sort_order는 환경에 맞게 수정하세요
INSERT INTO admin_menus (
    site_id,
    parent_id,
    name,
    code,
    type,
    url,
    icon,
    sort_order,
    status,
    target,
    roles,
    description,
    component_path,
    related_routes,
    created_at,
    updated_at
) VALUES (
    1,                                                          -- site_id: 사이트 ID
    NULL,                                                       -- parent_id: 최상위 메뉴
    '방문자 통계',                                               -- name: 메뉴명
    'analytics',                                                -- code: 고유 코드
    'INTERNAL',                                                 -- type: 내부 페이지
    '/adm/analytics',                                           -- url: 접근 URL
    'mdi-chart-line',                                           -- icon: 아이콘 (MDI 아이콘)
    2,                                                          -- sort_order: 정렬 순서 (대시보드 다음)
    'ACTIVE',                                                   -- status: 활성화
    '_self',                                                    -- target: 현재 창
    NULL,                                                       -- roles: 권한 (NULL = 모든 관리자)
    '방문자 접속 통계 및 분석',                                   -- description: 설명
    '@/views/default/common/analytics/AnalyticsView.vue',       -- component_path: Vue 컴포넌트 경로
    NULL,                                                       -- related_routes: 관련 라우트
    NOW(),
    NOW()
);

-- 확인
-- SELECT * FROM admin_menus WHERE code = 'analytics';
