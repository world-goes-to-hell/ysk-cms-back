# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Development Commands

```bash
# Build
./gradlew build

# Run application
./gradlew bootRun

# Run tests (JUnit 5)
./gradlew test

# Build without tests
./gradlew build -x test

# Start MariaDB with Docker
docker-compose up -d
```

## Architecture Overview

Spring Boot 3.2.1 기반 CMS 백엔드 애플리케이션. DDD 패턴의 도메인 레이어 구조 사용.

### Domain Layer Structure

각 도메인 모듈은 다음 계층으로 구성:
- `controller/` - REST API 엔드포인트
- `service/` - 비즈니스 로직
- `repository/` - Spring Data JPA 데이터 접근
- `entity/` - JPA 엔티티
- `dto/` - Request/Response DTO

### Core Domains

| Domain | Description |
|--------|-------------|
| auth | JWT 기반 인증/토큰 관리 |
| user | 사용자 CRUD, 상태 관리 |
| site | 멀티 사이트 설정 |
| menu | 네비게이션 메뉴 (componentPath, relatedRoutes 지원) |
| board | 게시판 관리 (유형별 설정) |
| article | 게시글 CRUD |
| reply | 댓글 기능 |
| atchfile | 첨부 파일 (GCS 스토리지) |
| activity | 활동 로그 |
| page | 정적/동적 페이지 |
| dashboard | 통계 대시보드 |

### Key Configuration Classes

- `SecurityConfig` - JWT 무상태 인증, CORS, 공개 URL 설정
- `GcsConfig` - Google Cloud Storage 연결
- `JpaAuditingConfig` - 엔티티 생성/수정일시 자동 관리
- `DataInitializer` - 초기 데이터 설정

## Technology Stack

- **Java 17**, **Spring Boot 3.2.1**, **Spring Data JPA**
- **MariaDB** (로컬), **CloudSQL MySQL** (GCP)
- **JWT** (JJWT 0.12.3) - Access 15분, Refresh 7일
- **Google Cloud Storage** - 파일 스토리지
- **SpringDoc OpenAPI 3** - Swagger UI (/swagger-ui.html)

## API Conventions

- 모든 응답은 `ApiResponse<T>` 래퍼 사용
- 예외는 `ErrorCode` enum으로 정의
- URL 패턴: `/api/sites/{siteCode}/[domain]/...`

## Database

- 로컬: `localhost:3306/ysk_cms` (root/root1234)
- GCP: CloudSQL Socket Factory 사용
- 마이그레이션 스크립트: `docs/database/`

## Commit Messages

한글로 작성. 예: `feat: 댓글 기능 추가`
