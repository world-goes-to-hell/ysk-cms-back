# YSK CMS Admin API 구현 계획

## 개요
Spring Boot 3.2.1 기반 멀티 사이트 CMS Admin API 서버 구현

## 요구사항 요약
- **멀티 사이트**: `/site1`, `/site2` 형태의 URL 경로 기반 사이트 구분
- **컨텐츠 관리**: 게시판/게시글, 정적 페이지, 미디어/파일
- **인증**: JWT 토큰 (Stateless)
- **권한**: RBAC (슈퍼관리자, 사이트관리자, 에디터, 뷰어)

---

## 진행 상황

| Phase | 상태 | 설명 |
|-------|------|------|
| Phase 1 | ✅ 완료 | 기반 인프라 |
| Phase 2 | ✅ 완료 | 멀티 사이트 |
| Phase 3 | ✅ 완료 | 콘텐츠 관리 |
| Phase 4 | ✅ 완료 | 미디어 관리 |

---

## 구현 우선순위

### Phase 1: 기반 인프라 ✅ 완료
- [x] 공통 Base Entity 및 JPA Auditing 설정
- [x] API 응답 표준화 (ApiResponse, ErrorCode)
- [x] 전역 예외 처리 (GlobalExceptionHandler)
- [x] JWT 인증 시스템 (JwtTokenProvider, JwtAuthenticationFilter)
- [x] Spring Security 설정 (SecurityConfig)
- [x] 사용자/역할/권한 도메인 (User, Role, Permission)
- [x] Auth API (로그인, 토큰 갱신, 현재 사용자)
- [x] 초기 데이터 설정 (DataInitializer)

### Phase 2: 멀티 사이트 ✅ 완료
- [x] Site 엔티티 및 관리 API
- [x] 사이트별 접근 제어
- [x] 토큰 만료 경고 헤더 (X-Token-Expires-In, X-Token-Expiring-Soon)
- [x] Public API 경로 (/api/public/**) 지원

### Phase 3: 콘텐츠 관리 ✅ 완료
- [x] Board (게시판) CRUD
- [x] Post (게시글) CRUD
- [x] Page (정적 페이지) CRUD

### Phase 4: 미디어 관리 ✅ 완료
- [x] Media 엔티티 및 관리 API
- [x] MinIO 연동 파일 업로드/다운로드
- [x] 다중 파일 업로드 지원
- [x] 이미지 크기 자동 추출
- [x] Presigned URL 생성

---

## 인프라 설정 ✅ 완료

### Docker 서비스
| 서비스 | 컨테이너 | 포트 | 용도 |
|--------|----------|------|------|
| MariaDB | ysk-cms-mariadb | 3306 | 데이터베이스 |
| MinIO | ysk-cms-minio | 9000 (API), 9001 (Console) | 파일 스토리지 |

### 실행 명령어
```bash
# 전체 서비스 시작
docker-compose up -d

# 서비스 상태 확인
docker-compose ps

# 서비스 중지
docker-compose down
```

---

## 생성된 파일 목록

### 공통 모듈
- `common/entity/BaseEntity.java` - 공통 엔티티
- `common/dto/ApiResponse.java` - API 응답 포맷
- `common/dto/PageResponse.java` - 페이징 응답 포맷
- `common/exception/ErrorCode.java` - 에러 코드 정의
- `common/exception/BusinessException.java` - 비즈니스 예외
- `common/exception/GlobalExceptionHandler.java` - 전역 예외 처리

### 설정
- `config/SecurityConfig.java` - Spring Security 설정
- `config/JpaAuditingConfig.java` - JPA Auditing 설정
- `config/MinioConfig.java` - MinIO 클라이언트 설정
- `config/DataInitializer.java` - 초기 데이터 설정
- `config/SwaggerConfig.java` - Swagger 설정
- `config/WebConfig.java` - CORS 설정

### 보안
- `security/jwt/JwtProperties.java` - JWT 설정 속성
- `security/jwt/JwtTokenProvider.java` - JWT 토큰 생성/검증
- `security/jwt/JwtAuthenticationFilter.java` - JWT 인증 필터
- `security/CustomUserDetails.java` - UserDetails 구현
- `security/UserDetailsServiceImpl.java` - UserDetailsService 구현

### 사용자 도메인
- `domain/user/entity/User.java` - 사용자 엔티티
- `domain/user/entity/Role.java` - 역할 엔티티
- `domain/user/entity/Permission.java` - 권한 엔티티
- `domain/user/entity/UserStatus.java` - 사용자 상태 Enum
- `domain/user/entity/ResourceType.java` - 리소스 타입 Enum
- `domain/user/entity/ActionType.java` - 액션 타입 Enum
- `domain/user/repository/UserRepository.java`
- `domain/user/repository/RoleRepository.java`
- `domain/user/repository/PermissionRepository.java`

### 인증 도메인
- `domain/auth/controller/AuthController.java` - 인증 API
- `domain/auth/service/AuthService.java` - 인증 서비스
- `domain/auth/dto/LoginRequest.java`
- `domain/auth/dto/LoginResponse.java`
- `domain/auth/dto/TokenRefreshRequest.java`
- `domain/auth/dto/TokenRefreshResponse.java`

### 사이트 도메인
- `domain/site/entity/Site.java` - 사이트 엔티티
- `domain/site/entity/SiteStatus.java` - 사이트 상태 Enum
- `domain/site/repository/SiteRepository.java`
- `domain/site/service/SiteService.java`
- `domain/site/controller/SiteController.java`
- `domain/site/dto/SiteDto.java, SiteCreateRequest.java, SiteUpdateRequest.java`

### 게시판 도메인
- `domain/board/entity/Board.java` - 게시판 엔티티
- `domain/board/entity/BoardType.java` - 게시판 유형 Enum (NORMAL, GALLERY, FAQ, QNA)
- `domain/board/entity/BoardStatus.java` - 게시판 상태 Enum
- `domain/board/repository/BoardRepository.java`
- `domain/board/service/BoardService.java`
- `domain/board/controller/BoardController.java`
- `domain/board/dto/BoardDto.java, BoardCreateRequest.java, BoardUpdateRequest.java`

### 게시글 도메인
- `domain/post/entity/Post.java` - 게시글 엔티티
- `domain/post/entity/PostStatus.java` - 게시글 상태 Enum (DRAFT, PUBLISHED, ARCHIVED)
- `domain/post/repository/PostRepository.java`
- `domain/post/service/PostService.java`
- `domain/post/controller/PostController.java`
- `domain/post/dto/PostDto.java, PostListDto.java, PostCreateRequest.java, PostUpdateRequest.java`

### 페이지 도메인
- `domain/page/entity/Page.java` - 페이지 엔티티 (Self-referencing)
- `domain/page/entity/PageStatus.java` - 페이지 상태 Enum
- `domain/page/repository/PageRepository.java`
- `domain/page/service/PageService.java`
- `domain/page/controller/PageController.java`
- `domain/page/dto/PageDto.java, PageListDto.java, PageCreateRequest.java, PageUpdateRequest.java`

### 미디어 도메인
- `domain/media/entity/Media.java` - 미디어 엔티티
- `domain/media/entity/MediaType.java` - 미디어 유형 Enum (IMAGE, VIDEO, AUDIO, DOCUMENT, ARCHIVE, OTHER)
- `domain/media/repository/MediaRepository.java`
- `domain/media/service/MediaService.java` - MinIO 연동
- `domain/media/controller/MediaController.java`
- `domain/media/dto/MediaDto.java, MediaListDto.java, MediaUploadRequest.java, MediaUpdateRequest.java`

---

## 패키지 구조

```
com.ysk.cms
├── common/
│   ├── entity/BaseEntity.java
│   ├── dto/ApiResponse.java, PageResponse.java
│   └── exception/GlobalExceptionHandler.java, ErrorCode.java, BusinessException.java
├── config/
│   ├── SecurityConfig.java
│   ├── JpaAuditingConfig.java
│   ├── MinioConfig.java
│   ├── DataInitializer.java
│   ├── SwaggerConfig.java
│   └── WebConfig.java
├── security/
│   ├── jwt/JwtProperties.java, JwtTokenProvider.java, JwtAuthenticationFilter.java
│   ├── CustomUserDetails.java
│   └── UserDetailsServiceImpl.java
└── domain/
    ├── auth/
    │   ├── controller/AuthController.java
    │   ├── service/AuthService.java
    │   └── dto/LoginRequest.java, LoginResponse.java, TokenRefresh*.java
    ├── user/
    │   ├── entity/User.java, Role.java, Permission.java
    │   └── repository/UserRepository.java, RoleRepository.java, PermissionRepository.java
    ├── site/
    │   ├── entity/Site.java, SiteStatus.java
    │   ├── repository/SiteRepository.java
    │   ├── service/SiteService.java
    │   ├── controller/SiteController.java
    │   └── dto/SiteDto.java, SiteCreateRequest.java, SiteUpdateRequest.java
    ├── board/
    │   ├── entity/Board.java, BoardType.java, BoardStatus.java
    │   ├── repository/BoardRepository.java
    │   ├── service/BoardService.java
    │   ├── controller/BoardController.java
    │   └── dto/BoardDto.java, BoardCreateRequest.java, BoardUpdateRequest.java
    ├── post/
    │   ├── entity/Post.java, PostStatus.java
    │   ├── repository/PostRepository.java
    │   ├── service/PostService.java
    │   ├── controller/PostController.java
    │   └── dto/PostDto.java, PostListDto.java, PostCreateRequest.java, PostUpdateRequest.java
    ├── page/
    │   ├── entity/Page.java, PageStatus.java
    │   ├── repository/PageRepository.java
    │   ├── service/PageService.java
    │   ├── controller/PageController.java
    │   └── dto/PageDto.java, PageListDto.java, PageCreateRequest.java, PageUpdateRequest.java
    └── media/
        ├── entity/Media.java, MediaType.java
        ├── repository/MediaRepository.java
        ├── service/MediaService.java
        ├── controller/MediaController.java
        └── dto/MediaDto.java, MediaListDto.java, MediaUploadRequest.java, MediaUpdateRequest.java
```

---

## 핵심 Entity 설계

| Entity | 주요 필드 | 관계 | 상태 |
|--------|----------|------|------|
| User | username, password, email, status | ManyToMany: Role, Site | ✅ 완료 |
| Role | name (SUPER_ADMIN, SITE_ADMIN, EDITOR, VIEWER) | ManyToMany: Permission | ✅ 완료 |
| Permission | name, resourceType, actionType | - | ✅ 완료 |
| Site | code, name, domain, status, settings(JSON) | OneToMany: Board, Page | ✅ 완료 |
| Board | site, code, name, type, useComment | OneToMany: Post | ✅ 완료 |
| Post | board, title, content, status, isPinned | OneToMany: Attachment | ✅ 완료 |
| Page | site, slug, title, content, status | Self-referencing (parent) | ✅ 완료 |
| Media | site, originalName, filePath, mimeType, type | - | ✅ 완료 |

---

## API 엔드포인트

### 인증 ✅ 완료
- `POST /api/auth/login` - 로그인
- `POST /api/auth/refresh` - 토큰 갱신
- `GET /api/auth/me` - 현재 사용자

### 사용자 관리 ⏳ 대기
- `GET/POST /api/users` - 목록/생성
- `GET/PUT/DELETE /api/users/{id}` - 상세/수정/삭제

### 사이트 관리 ✅ 완료
- `GET/POST /api/sites` - 목록/생성
- `GET/PUT/DELETE /api/sites/{code}` - 상세/수정/삭제

### 게시판/게시글 (사이트별) ✅ 완료
- `/api/sites/{siteCode}/boards` - 게시판 CRUD
- `/api/sites/{siteCode}/boards/{boardCode}/posts` - 게시글 CRUD

### 페이지 (사이트별) ✅ 완료
- `/api/sites/{siteCode}/pages` - 페이지 CRUD

### 미디어 ✅ 완료
- `GET /api/media` - 전체 미디어 목록
- `GET /api/sites/{siteCode}/media` - 사이트별 미디어 목록
- `POST /api/sites/{siteCode}/media/upload` - 파일 업로드
- `POST /api/sites/{siteCode}/media/upload-multiple` - 다중 파일 업로드
- `GET /api/media/{id}` - 미디어 상세
- `PUT /api/media/{id}` - 미디어 정보 수정
- `DELETE /api/media/{id}` - 미디어 삭제
- `GET /api/media/{id}/download` - 파일 다운로드
- `GET /api/media/{id}/presigned-url` - Presigned URL 조회

---

## 의존성 (build.gradle)

```gradle
// Spring Boot Starters
implementation 'org.springframework.boot:spring-boot-starter-web'
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
implementation 'org.springframework.boot:spring-boot-starter-validation'

// Database
runtimeOnly 'org.mariadb.jdbc:mariadb-java-client'

// Swagger (OpenAPI 3)
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0'

// Security & JWT
implementation 'org.springframework.boot:spring-boot-starter-security'
implementation 'io.jsonwebtoken:jjwt-api:0.12.3'
runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.3'
runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.3'

// MinIO (S3 호환 오브젝트 스토리지)
implementation 'io.minio:minio:8.5.7'

// Lombok
compileOnly 'org.projectlombok:lombok'
annotationProcessor 'org.projectlombok:lombok'
```

---

## 검증 방법

1. **빌드 테스트**: `./gradlew build`
2. **애플리케이션 실행**: `./gradlew bootRun`
3. **Swagger UI 확인**: http://localhost:8080/swagger-ui.html
4. **MinIO Console**: http://localhost:9001
5. **API 테스트**:
   - 로그인 API 호출 → JWT 토큰 발급 확인
   - 토큰으로 보호된 API 호출 → 권한 검증 확인

---

## 접속 정보

### 기본 관리자 계정
| 항목 | 값 |
|------|-----|
| Username | admin |
| Password | admin1234 |
| Role | SUPER_ADMIN |

### MariaDB
| 항목 | 값 |
|------|-----|
| Host | localhost |
| Port | 3306 |
| Database | ysk_cms |
| Username | root |
| Password | root1234 |

### MinIO
| 항목 | 값 |
|------|-----|
| API Endpoint | http://localhost:9000 |
| Console URL | http://localhost:9001 |
| Access Key | minioadmin |
| Secret Key | minioadmin1234 |
| Bucket | ysk-cms |
