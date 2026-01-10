# YSK CMS

Spring Boot 기반 CMS 프로젝트

## 기술 스택

- Java 17
- Spring Boot 3.2.1
- Spring Data JPA
- MariaDB 11.2
- Gradle 8.5
- Lombok

## 프로젝트 구조

```
ysk-cms/
├── build.gradle
├── settings.gradle
├── docker-compose.yml
├── gradlew / gradlew.bat
├── gradle/wrapper/
│   ├── gradle-wrapper.jar
│   └── gradle-wrapper.properties
└── src/
    ├── main/
    │   ├── java/com/ysk/cms/
    │   │   └── YskCmsApplication.java
    │   └── resources/
    │       └── application.yml
    └── test/java/com/ysk/cms/
        └── YskCmsApplicationTests.java
```

## 환경 설정

### 사전 요구사항

- JDK 17
- Docker Desktop

### 데이터베이스 설정

| 항목 | 값 |
|------|-----|
| Host | localhost |
| Port | 3306 |
| Database | ysk_cms |
| Username | root |
| Password | root1234 |

## 실행 방법

### 1. MariaDB 실행

```bash
# MariaDB 컨테이너 시작
docker-compose up -d

# 상태 확인
docker-compose ps

# 로그 확인
docker-compose logs -f mariadb

# 중지
docker-compose down
```

### 2. 애플리케이션 실행

```bash
# 빌드
./gradlew build

# 실행
./gradlew bootRun
```

애플리케이션은 http://localhost:8080 에서 실행됩니다.

## 의존성

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    runtimeOnly 'org.mariadb.jdbc:mariadb-java-client'
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```
