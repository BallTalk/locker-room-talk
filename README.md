# 📦 Gradle 프로젝트 구조 가이드

이 프로젝트는 **공통 설정, 버전 관리, 의존성 관리**를 아래와 같이 분리하여 구성합니다.  
각 항목의 역할에 맞게 설정을 추가해 주세요.

---

## ✅ 1. `buildSrc/src/main/groovylocker/java-conventions.gradle`

- 모든 모듈에 공통으로 적용되는 Gradle 설정을 정의합니다.
- JDK 버전, 공통 플러그인, 디펜던시, 리포지토리 등은 이 파일에서 설정합니다.

---

## ✅ 2. `gradle/libs.versions.toml`

- 모든 라이브러리의 버전과 모듈 정보를 중앙에서 관리합니다.
- 버전이 변경되거나 새로운 라이브러리를 추가할 경우 이 파일을 수정합니다.

---

## ✅ 3. 각 모듈의 `build.gradle`

- 개별 모듈에서는 공통 설정을 별도로 작성하지 않아도 됩니다.
- 필요한 의존성만 `libs.xxx` 형태로 선언하여 간결하게 관리합니다.



<br>




# 🐳 Docker 환경 구성 가이드

로컬 개발 환경은 **Docker Compose**를 통해 MySQL·Redis 컨테이너를 기동하고,  
테스트 환경은 **Testcontainers**를 이용해 MySQL 컨테이너를 띄웁니다.  
각 실행 시마다 `schema.sql`·`data.sql`으로 데이터를 초기화하므로,  
개인 로컬 환경에 영향을 받지 않고 깨끗한 상태에서 개발·테스트를 진행할 수 있습니다.

> ⚠️ **주의:** Docker Compose 컨테이너가 실행 중이지 않으면  
> - `local` 프로필로 애플리케이션 실행  
> - `Testcontainers` 기반 통합 테스트
>
> 모두 데이터베이스에 연결할 수 없어 실패합니다.  
> `application.yml`에 설정된 데이터소스 URL·계정 정보가 모두 Docker 환경(MySQL 컨테이너)에 의존하기 때문입니다.
> 
> 💡 SQL 초기화 설정(`spring.sql.init.mode: always`)을 제거하면, 볼륨에 남아 있는 데이터 파일을 계속 활용할 수 있습니다.  

---

## ✅ 1. `docker-compose.yml`

- 로컬 개발용 MySQL · Redis 컨테이너 정의  
- MySQL 데이터는 `./data/mysql`에 바인드 마운트하여 유지  
- Redis는 캐시 특성상 볼륨 설정은 선택사항

---

## ✅ 2. Testcontainers 설정

- 테스트 실행 시 MySQL 컨테이너를 자동으로 띄우고 초기화  
- `TestcontainersConfiguration` 클래스에 이미지, DB 이름·사용자·비밀번호 설정  
- JVM 시스템 프로퍼티로 Spring Datasource 정보 주입  
- `@PreDestroy` 로 테스트 종료 시 컨테이너 중지

---

## ✅ 3. `application.yml`

- **프로필별(local/test)** 로 구분된 DB · 로깅 설정  
  - `local` : Docker Compose MySQL 사용  
  - `test`  : local 설정을 덮어쓰고 Testcontainers MySQL 사용  
- SQL 초기화(`schema.sql`·`data.sql`) 및 Hibernate DDL 옵션 설정  


