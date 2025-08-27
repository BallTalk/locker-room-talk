# CLAUDE.md

이 파일은 Claude Code (claude.ai/code)가 이 저장소에서 작업할 때 필요한 가이드를 제공합니다.

## 빌드 및 개발 명령어

### 빌드 및 테스트
- `./gradlew build` - 모든 모듈 빌드
- `./gradlew test` - 모든 테스트 실행
- `./gradlew jacocoTestReport` - 테스트 커버리지 리포트 생성
- `./gradlew :app:test` - app 모듈 테스트만 실행
- `./gradlew :account:test` - account 모듈 테스트만 실행

### 애플리케이션 실행
- `./gradlew bootRun` - Spring Boot 애플리케이션 실행
- 먼저 Docker 컨테이너가 실행 중이어야 함: `docker-compose up -d`

### Docker 환경
- `docker-compose up -d` - 로컬 개발용 MySQL 및 Redis 컨테이너 시작
- `docker-compose down` - 컨테이너 중지 및 제거
- `docker-compose logs -f` - 컨테이너 로그 확인

## 아키텍처 개요

이 프로젝트는 멀티 모듈 Gradle 구성을 사용하는 모듈화된 Spring Boot 애플리케이션입니다:

### 모듈 구조
- **app** - `TalkMainApplication.java`가 포함된 메인 Spring Boot 애플리케이션 모듈
- **account** - 인증 및 사용자 계정 관리
  - **auth** - 로그인, JWT, OAuth2, SMS 인증
  - **user** - 사용자 도메인, 프로필 관리, 비밀번호 작업
- **common** - 공통 유틸리티, 예외 처리, 필터, Aspect
- **test-support** - 테스트 픽스쳐 및 Testcontainers 설정

### 주요 아키텍처 패턴
- **Domain-Driven Design** - 각 도메인 모듈에 `api`, `application`, `domain`, `infra` 레이어 포함
- **Hexagonal Architecture** - 애플리케이션 코어와 외부 어댑터 간의 명확한 분리
- **JWT + OAuth2 인증** - Google 및 Kakao 소셜 로그인을 JWT 토큰과 함께 지원
- **Multi-tenancy 지원** - 세션 관리 및 JWT 블랙리스트를 위한 Redis 사용

### 데이터베이스 및 인프라
- **MySQL 8.0** - SQL 스크립트를 통한 스키마/데이터 초기화를 포함한 메인 데이터베이스
- **Redis** - 세션 저장소, JWT 블랙리스트, SMS 인증 코드
- **Testcontainers** - 자동화된 테스트 데이터베이스 프로비저닝
- **Docker Compose** - 로컬 개발 환경 설정

### 설정 관리
- **Gradle Version Catalog** - `gradle/libs.versions.toml`에서 중앙화된 의존성 버전 관리
- **Java Conventions** - `buildSrc/src/main/groovy/locker.java-conventions.gradle`에서 공유 빌드 설정
- **환경 변수** - `.env` 파일을 통한 설정 (커밋되지 않음)
- **프로필 기반 설정** - `application.yml`에서 `local` 및 `test` 프로필

### 인증 플로우
- Redis 블랙리스트를 지원하는 JWT 기반 인증
- Google 및 Kakao 프로바이더와의 OAuth2 통합
- 비밀번호 재설정 및 계정 작업을 위한 SMS 인증
- 사용자 컨텍스트를 위한 커스텀 보안 필터 및 리졸버

### 테스트 전략
- **통합 테스트** - 데이터베이스에 의존하는 테스트는 Testcontainers 사용
- **단위 테스트** - 외부 의존성 없이 비즈니스 로직에 집중
- **Smoke 테스트** - 애플리케이션 시작 및 기본 기능 확인
- 데이터베이스 통합 테스트를 위해 `TestcontainersConfiguration` 임포트

## ⚠️ 주의사항
- 모든 테스트 및 로컬 개발은 Docker 컨테이너가 실행 중이어야 함
- 데이터베이스 스키마 및 데이터는 각 애플리케이션 시작 시 SQL 초기화를 통해 리셋됨
- JWT 토큰은 로그아웃 기능을 위해 Redis 블랙리스트에 저장됨
- SMS 인증은 CoolSMS 서비스 통합 사용