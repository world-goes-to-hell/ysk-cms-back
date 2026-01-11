-- =============================================
-- boards 테이블에 type_code 컬럼 추가 마이그레이션
-- 기존 type 컬럼 → type_code 컬럼으로 변경
-- =============================================

-- 1. type_code 컬럼 추가
ALTER TABLE boards
    ADD COLUMN type_code VARCHAR(50) NOT NULL DEFAULT 'NORMAL' AFTER description;

-- 2. 기존 type 컬럼 데이터를 type_code로 복사
UPDATE boards SET type_code = type;

-- 3. 기존 type 컬럼 삭제
ALTER TABLE boards DROP COLUMN type;

-- =============================================
-- 마이그레이션 완료 확인
-- =============================================
SELECT 'Migration completed successfully' AS status;
DESCRIBE boards;
