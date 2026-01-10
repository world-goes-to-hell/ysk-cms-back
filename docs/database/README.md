# YSK CMS Database Documentation

## 개요

YSK CMS는 MariaDB 11.2를 사용하는 멀티 사이트 CMS 시스템입니다.

## 파일 구성

| 파일 | 설명 |
|------|------|
| `01_schema.sql` | 테이블 생성 DDL |
| `02_initial_data.sql` | 초기 데이터 (역할, 권한, 관리자 계정) |
| `03_sample_data.sql` | 샘플 데이터 (테스트용, 선택사항) |

## 설치 순서

```bash
# 1. 스키마 생성
mysql -u root -p ysk_cms < 01_schema.sql

# 2. 초기 데이터 입력
mysql -u root -p ysk_cms < 02_initial_data.sql

# 3. (선택) 샘플 데이터 입력
mysql -u root -p ysk_cms < 03_sample_data.sql
```

## ERD (Entity Relationship Diagram)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              USER MANAGEMENT                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌──────────┐      ┌────────────┐      ┌──────────┐                         │
│  │  users   │──M:N─│ user_roles │──M:N─│  roles   │                         │
│  └──────────┘      └────────────┘      └──────────┘                         │
│       │                                      │                               │
│       │                              ┌───────────────┐                       │
│       │                              │role_permissions│                      │
│       │                              └───────────────┘                       │
│       │                                      │                               │
│       │                              ┌──────────────┐                        │
│  ┌────────────┐                      │ permissions  │                        │
│  │ user_sites │                      └──────────────┘                        │
│  └────────────┘                                                              │
│       │                                                                      │
└───────┼──────────────────────────────────────────────────────────────────────┘
        │
        ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                              SITE MANAGEMENT                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌──────────┐                                                                │
│  │  sites   │                                                                │
│  └──────────┘                                                                │
│       │                                                                      │
│       ├──────────────────┬──────────────────┬──────────────────┐            │
│       ▼                  ▼                  ▼                  ▼            │
│  ┌──────────┐      ┌──────────┐      ┌──────────┐      ┌──────────┐         │
│  │  boards  │      │  pages   │      │  media   │      │ activity │         │
│  └──────────┘      └──────────┘      └──────────┘      │  _logs   │         │
│       │                 │                              └──────────┘         │
│       ▼                 │                                                    │
│  ┌──────────┐           │ (self-referencing)                                │
│  │  posts   │           └──────────┘                                        │
│  └──────────┘                                                                │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

## 테이블 상세

### 1. 사용자 관리

#### users (사용자)
| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGINT | PK |
| username | VARCHAR(50) | 로그인 ID (UNIQUE) |
| password | VARCHAR(255) | BCrypt 암호화된 비밀번호 |
| email | VARCHAR(100) | 이메일 (UNIQUE) |
| name | VARCHAR(50) | 이름 |
| status | VARCHAR(20) | 상태 |
| last_login_at | DATETIME | 마지막 로그인 시간 |
| created_at | DATETIME | 생성일시 |
| updated_at | DATETIME | 수정일시 |

#### roles (역할)
| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGINT | PK |
| name | VARCHAR(50) | 역할명 (UNIQUE) |
| description | VARCHAR(200) | 설명 |

#### permissions (권한)
| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGINT | PK |
| name | VARCHAR(100) | 권한명 (UNIQUE) |
| description | VARCHAR(200) | 설명 |
| resource_type | VARCHAR(20) | 리소스 타입 |
| action_type | VARCHAR(20) | 액션 타입 |

### 2. 사이트 관리

#### sites (사이트)
| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGINT | PK |
| code | VARCHAR(50) | 사이트 코드 (UNIQUE) |
| name | VARCHAR(100) | 사이트명 |
| description | VARCHAR(500) | 설명 |
| domain | VARCHAR(255) | 도메인 |
| status | VARCHAR(20) | 상태 |
| settings | JSON | 사이트 설정 |

### 3. 콘텐츠 관리

#### boards (게시판)
| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGINT | PK |
| site_id | BIGINT | FK → sites |
| code | VARCHAR(50) | 게시판 코드 |
| name | VARCHAR(100) | 게시판명 |
| type | VARCHAR(20) | 게시판 유형 |
| use_comment | TINYINT(1) | 댓글 사용 여부 |
| use_attachment | TINYINT(1) | 첨부파일 사용 여부 |
| attachment_limit | INT | 첨부파일 개수 제한 |
| sort_order | INT | 정렬 순서 |
| status | VARCHAR(20) | 상태 |

#### posts (게시글)
| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGINT | PK |
| board_id | BIGINT | FK → boards |
| title | VARCHAR(300) | 제목 |
| content | LONGTEXT | 내용 |
| author | VARCHAR(50) | 작성자 |
| view_count | INT | 조회수 |
| is_pinned | TINYINT(1) | 상단 고정 여부 |
| is_secret | TINYINT(1) | 비밀글 여부 |
| status | VARCHAR(20) | 상태 |
| published_at | DATETIME | 발행일시 |
| answer | TEXT | 답변 (Q&A용) |

#### pages (정적 페이지)
| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGINT | PK |
| site_id | BIGINT | FK → sites |
| parent_id | BIGINT | FK → pages (자기참조) |
| slug | VARCHAR(100) | URL 슬러그 |
| title | VARCHAR(200) | 제목 |
| content | LONGTEXT | 내용 |
| meta_description | VARCHAR(300) | SEO 설명 |
| meta_keywords | VARCHAR(500) | SEO 키워드 |
| status | VARCHAR(20) | 상태 |
| published_at | DATETIME | 발행일시 |
| sort_order | INT | 정렬 순서 |

### 4. 미디어 관리

#### media (미디어)
| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGINT | PK |
| site_id | BIGINT | FK → sites |
| original_name | VARCHAR(255) | 원본 파일명 |
| stored_name | VARCHAR(500) | 저장된 파일명 |
| file_path | VARCHAR(1000) | 파일 경로 |
| mime_type | VARCHAR(100) | MIME 타입 |
| file_size | BIGINT | 파일 크기 (bytes) |
| type | VARCHAR(20) | 미디어 유형 |
| description | VARCHAR(500) | 설명 |
| alt_text | VARCHAR(500) | 대체 텍스트 |
| width | INT | 너비 (이미지) |
| height | INT | 높이 (이미지) |

### 5. 활동 로그

#### activity_logs (활동 로그)
| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGINT | PK |
| user_id | BIGINT | FK → users |
| site_id | BIGINT | FK → sites |
| activity_type | VARCHAR(20) | 활동 유형 |
| target_type | VARCHAR(50) | 대상 타입 |
| target_id | BIGINT | 대상 ID |
| target_name | VARCHAR(200) | 대상 이름 |
| description | VARCHAR(500) | 상세 설명 |
| ip_address | VARCHAR(50) | IP 주소 |
| created_at | DATETIME | 생성일시 |

## ENUM 값 정의

### UserStatus (사용자 상태)
| 값 | 설명 |
|----|------|
| `ACTIVE` | 활성 |
| `INACTIVE` | 비활성 |
| `LOCKED` | 잠김 |
| `WITHDRAWN` | 탈퇴 |

### SiteStatus (사이트 상태)
| 값 | 설명 |
|----|------|
| `ACTIVE` | 활성 |
| `INACTIVE` | 비활성 |
| `MAINTENANCE` | 점검 중 |

### BoardType (게시판 유형)
| 값 | 설명 |
|----|------|
| `NORMAL` | 일반 게시판 |
| `GALLERY` | 갤러리 |
| `QNA` | Q&A |
| `FAQ` | FAQ |
| `NOTICE` | 공지사항 |

### BoardStatus (게시판 상태)
| 값 | 설명 |
|----|------|
| `ACTIVE` | 활성 |
| `INACTIVE` | 비활성 |

### PostStatus (게시글 상태)
| 값 | 설명 |
|----|------|
| `DRAFT` | 임시저장 |
| `PUBLISHED` | 발행됨 |
| `ARCHIVED` | 보관됨 |
| `DELETED` | 삭제됨 |

### PageStatus (페이지 상태)
| 값 | 설명 |
|----|------|
| `DRAFT` | 임시저장 |
| `PUBLISHED` | 발행됨 |
| `ARCHIVED` | 보관됨 |

### MediaType (미디어 유형)
| 값 | 설명 |
|----|------|
| `IMAGE` | 이미지 |
| `VIDEO` | 비디오 |
| `AUDIO` | 오디오 |
| `DOCUMENT` | 문서 |
| `OTHER` | 기타 |

### ActivityType (활동 유형)
| 값 | 설명 |
|----|------|
| `CREATE` | 생성 |
| `UPDATE` | 수정 |
| `DELETE` | 삭제 |
| `LOGIN` | 로그인 |
| `LOGOUT` | 로그아웃 |
| `VIEW` | 조회 |
| `PUBLISH` | 발행 |
| `UPLOAD` | 업로드 |

### ResourceType (리소스 유형)
| 값 | 설명 |
|----|------|
| `USER` | 사용자 |
| `SITE` | 사이트 |
| `BOARD` | 게시판 |
| `POST` | 게시글 |
| `PAGE` | 페이지 |
| `MEDIA` | 미디어 |

### ActionType (액션 유형)
| 값 | 설명 |
|----|------|
| `CREATE` | 생성 |
| `READ` | 조회 |
| `UPDATE` | 수정 |
| `DELETE` | 삭제 |

## 기본 역할 및 권한

| 역할 | 설명 | 권한 |
|------|------|------|
| SUPER_ADMIN | 슈퍼 관리자 | 모든 권한 |
| SITE_ADMIN | 사이트 관리자 | 사이트 조회/수정, 콘텐츠 전체 |
| EDITOR | 에디터 | 콘텐츠 생성/조회/수정 |
| VIEWER | 뷰어 | 조회만 가능 |

## 기본 계정

| 계정 | 비밀번호 | 역할 |
|------|----------|------|
| admin | admin123 | SUPER_ADMIN |
