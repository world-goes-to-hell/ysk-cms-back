# YSK CMS

Spring Boot 기반 엔터프라이즈 CMS 백엔드 애플리케이션

## 기술 스택

- **Backend**: Java 17, Spring Boot 3.2.1, Spring Data JPA
- **Security**: Spring Security, JWT (JJWT 0.12.3)
- **Database**: MariaDB 11.2 (로컬), CloudSQL MySQL (GCP)
- **Storage**: Google Cloud Storage
- **API Docs**: SpringDoc OpenAPI 3 (Swagger UI)
- **Build**: Gradle 8.5
- **Deploy**: Google Cloud Run, Docker

## 프로젝트 구조

```
src/main/java/com/ysk/cms/
├── YskCmsApplication.java
├── config/                    # 설정 클래스
│   ├── SecurityConfig.java    # JWT 인증, CORS
│   ├── GcsConfig.java         # Google Cloud Storage
│   └── ...
├── common/                    # 공통 모듈
│   ├── dto/                   # ApiResponse 등
│   ├── entity/                # BaseEntity
│   └── exception/             # ErrorCode, 예외 처리
└── domain/                    # 도메인 모듈 (DDD 패턴)
    ├── auth/                  # 인증 (로그인, 토큰)
    ├── user/                  # 사용자 관리
    ├── site/                  # 사이트 설정
    ├── menu/                  # 메뉴 관리
    ├── board/                 # 게시판 관리
    ├── article/               # 게시글
    ├── reply/                 # 댓글
    ├── atchfile/              # 첨부 파일
    ├── activity/              # 활동 로그
    ├── page/                  # 페이지 관리
    └── dashboard/             # 대시보드
```

각 도메인은 `controller/`, `service/`, `repository/`, `entity/`, `dto/` 계층으로 구성

## 환경 설정

### 사전 요구사항

- JDK 17
- Docker Desktop (로컬 DB용)

### 데이터베이스 설정 (로컬)

| 항목 | 값 |
|------|-----|
| Host | localhost |
| Port | 3306 |
| Database | ysk_cms |
| Username | root |
| Password | root1234 |

## 실행 방법

### 1. MariaDB 실행 (로컬)

```bash
# MariaDB 컨테이너 시작
docker-compose up -d

# 상태 확인
docker-compose ps

# 중지
docker-compose down
```

### 2. 애플리케이션 실행

```bash
# 빌드
./gradlew build

# 실행
./gradlew bootRun

# 테스트
./gradlew test

# 테스트 제외 빌드
./gradlew build -x test
```

애플리케이션: http://localhost:8080

## API 문서

Swagger UI: http://localhost:8080/swagger-ui.html

### 주요 API 엔드포인트

| 경로 | 설명 |
|------|------|
| `/api/auth/**` | 인증 (로그인, 토큰 갱신) |
| `/api/sites/{siteCode}/users/**` | 사용자 관리 |
| `/api/sites/{siteCode}/menus/**` | 메뉴 관리 |
| `/api/sites/{siteCode}/boards/**` | 게시판 관리 |
| `/api/sites/{siteCode}/boards/{boardCode}/articles/**` | 게시글 |
| `/api/sites/{siteCode}/atch-files/**` | 파일 업로드/다운로드 |
| `/api/sites/{siteCode}/activity-logs/**` | 활동 로그 |

## 주요 기능

- **인증**: JWT 기반 (Access 15분, Refresh 7일)
- **멀티 사이트**: siteCode 기반 멀티테넌시
- **게시판**: 유형별 설정 (일반, 공지, 갤러리, FAQ, Q&A)
- **파일 관리**: GCS 스토리지 연동
- **활동 로그**: 사용자 행동 기록 및 조회

## GCP 배포

```bash
# Docker 이미지 빌드
docker build -t gcr.io/[PROJECT_ID]/ysk-cms .

# Cloud Run 배포
gcloud run deploy ysk-cms --image gcr.io/[PROJECT_ID]/ysk-cms
```

## 데이터베이스 마이그레이션

마이그레이션 스크립트: `docs/database/`

```
01_schema.sql
02_initial_data.sql
...
07_migration_board_options.sql
```
