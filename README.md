## Spring Boot Backend Boilerplate

## 목차

* [개요](#개요)
* [개발 환경](#개발-환경)
* [Library](#library)
* [Profile](#profile)
* [Package](#package)
* [Filename Prefix and Suffix](#Filename-Prefix-and-Suffix)
* [Method Prefix and Suffix](#Method-Prefix-and-Suffix)
* [Response Format](#Response-Format)
* [Response Code](#Response-Code)
* [Static Analysis Tools](#static-analysis-tools)

---

### 개요

Spring Boot를 기반으로 한 Boilerplate 프로젝트입니다. 개발 초기 설정에 필요한 필수 구성 요소와 주요 라이브러리를 포함하고 있습니다.

### 개발 환경

- **IDE**: IntelliJ IDEA 2024.2
- **JDK**: 17
- **Spring Boot**: 3.4.0
- **Build Tool**: Gradle 8.10.2
- **MySQL**: Docker 컨테이너에서 실행되며, 설정은 `docker.compose` 폴더에 `docker-compose.yml`에 정의
- **Redis**: Docker 컨테이너에서 실행되며, 설정은 `docker.compose` 폴더에 `docker-compose.yml`에 정의
- **샘플 데이터 파일 경로**: `script/sample/data.sql` 및 `schema.sql`
- **로그 설정**: `logback-spring`을 사용하여 설정

---

### Library

* **Spring Boot**
    - `spring-boot-starter-web`: 웹 애플리케이션 개발을 위한 기본 설정과 의존성 제공
    - `spring-boot-starter-aop`: AOP(Aspect-Oriented Programming) 지원
    - `spring-boot-starter-validation`: 데이터 유효성 검증 지원
    - `spring-boot-starter-security`: 보안 기능 제공
    - `spring-boot-configuration-processor`: 애플리케이션 구성 프로세서
    - `spring-boot-starter-tomcat`: 임베디드 Tomcat 지원 (runtime 전용)
    - `spring-boot-starter-test`: 테스트 지원

* **Jasypt**
    - `jasypt-spring-boot-starter`: 속성값 암호화를 위한 기능 제공

* **Database**
    - `mybatis-spring-boot-starter`: MyBatis 통합 지원
    - `spring-boot-starter-data-jpa`: Spring Data JPA 지원
    - `mysql-connector-j`: MySQL 데이터베이스 커넥터

* **QueryDSL**
    - `querydsl-core`: QueryDSL의 주요 기능 모듈
    - `querydsl-jpa`: QueryDSL JPA 모듈
    - `querydsl-apt`: QueryDSL 어노테이션 프로세서
    - `jakarta.persistence-api`: Jakarta Persistence API
    - `jakarta.annotation-api`: Jakarta 어노테이션 API

* **Redis**
    - `spring-boot-starter-data-redis`: Redis 연동을 위한 기본 설정 및 지원

* **Lombok**
    - `lombok`: 반복적인 코드를 줄이기 위한 어노테이션 제공 (compileOnly 및 테스트 조합 사용)

* **MapStruct**
    - `mapstruct`: 객체 간의 매핑을 지원
    - `mapstruct-processor`: 컴파일 타임에 매핑 코드를 자동 생성

* **Apache Commons**
    - `commons-io`: Apache Commons IO 유틸리티
    - `commons-lang3`: Apache Commons Lang 유틸리티
    - `commons-text`: Apache Commons Text 유틸리티

* **JJWT**
    - `jjwt-api`: JWT 기능 제공
    - `jjwt-impl`: JWT 구현체 라이브러리 (runtime 전용)
    - `jjwt-jackson`: JWT와 Jackson 통합 라이브러리 (runtime 전용)

---

### Profile

* `application.yml`
    * `spring.profiles.active`는 `local`, `dev`로 구분
    * `spring.profiles.active` 값에 따라서 import 목록을 구분
        * `database-*.yml` - DataSource 설정
        * `environment-*.yml` - 환경 설정

---

### Package

* **Controller** 관련된 클래스는 `com.example.boilerplate.*.controller` 하위에 작성
* **Service** 관련된 클래스는 `com.example.boilerplate.*.service` 하위에 작성
* **Dto** 관련된 클래스는 `com.example.boilerplate.*.dto` 하위에 작성
* **Entity** 관련된 클래스는 `com.example.boilerplate.domain.entity` 하위에 작성
* **Entity Repository** 관련된 클래스 및 인터페이스는 `com.example.boilerplate.domain.repository` 하위에 작성
  → **Entity**와 직접적으로 연관된 클래스 및 인터페이스로, `JpaRepository` 등을 활용하여 기본적인 CRUD(저장, 조회, 수정, 삭제) 작업을 처리하는
  경우
* **Custom Repository** 관련된 클래스 및 인터페이스는 `com.example.boilerplate.*.repository` 하위에 작성  
  → **복잡한 Query**를 처리하기 위한 클래스 및 인터페이스로, **QueryDSL**와 같이 조건이 많거나 동적 SQL 처리가 필요한 경우
* **Mapper** 관련된 클래스는 `com.example.boilerplate.*.mapper` 하위에 작성, SQLMapper는 `resource.mapper.*` 하위에
  작성
* **Mapstruct** 관련된 인터페이스는 `com.example.boilerplate.*.mapstruct` 하위에 작성

---

### Filename Prefix and Suffix

#### Filename suffix

* `XXXController`: 요청값을 검사하고 응답값을 반환하기 위한 클래스
* `XXXService`: 비즈니스 로직을 처리하기 위한 클래스
* `XXXDto`: 파라미터를 전달받기 위한 클래스
* `XXXRepository`: `JpaRepository`를 상속받아 Entity의 기본적인 CRUD(저장, 조회, 수정, 삭제) 작업을 처리하거나, 복잡한 Query 처리를
  위한 메서드를 정의하는 클래스 및 인터페이스
* `XXXMapStruct`: Entity와 Dto 간의 매핑을 위한 인터페이스
* `XXXSpecification`: JPA Specification 사용하여 다중 조건을 구현하기 위한 클래스
* `XXXMapper`: 저장 프로시저나 SQL문으로 객체를 연결하기 위한 클래스

---

### Method Prefix and Suffix

#### Controller, Service Method Prefix

* `getXXX`: 객체를 반환하는 경우
* `setXXX`: 객체를 설정하는 경우
* `buildXXX`: 객체를 빌더 패턴으로 설정하는 경우
* `insertXXX`: 객체를 저장하는 경우
* `updateXXX`: 객체를 수정하는 경우
* `deleteXXX`: 객체를 삭제하는 경우

#### Mapper, Repository Method Prefix

* `selectXXX`: select 질의문으로 단일행을 조회하는 경우
* `selectXXXs`: select 질의문으로 다중행을 조회하는 경우
* `selectXXXCount`: select 질의문으로 갯수를 조회하는 경우
* `insertXXX`: insert 질의문으로 단일행을 저장하는 경우
* `insertXXXs`: insert 질의문으로 다중행을 저장하는 경우
* `updateXXX`: update 질의문으로 단일행을 수정하는 경우
* `updateXXXs`: update 질의문으로 다중행을 수정하는 경우

---

### Response Format

* `ExceptionAdvice.java` 는 공통 예외 처리를 관리하면, 예외 상황에 대해 적절한 HTTP 상태 코드를 설정하고 ErrorResponse 객체를 반환
* 각 `*Controller` 클래스는 성공적인 요청에 대해 `BaseResponse` 객체로 응답을 반환

---

### Response Code

#### 정상 코드

* `200`: 성공

#### Custom Exception 오류 코드

* 8xx: 기본 메시지는 동적으로 변경하여 응답
    * `800`: 오류가 발생했습니다. 확인 후 다시 시도해주세요.
    * `801`: 유효하지 않은 요청입니다.
    * `802`: 허용되지 않은 요청입니다.
    * `803`: 중복된 요청입니다.
    * `804`: 존재하지 않는 정보입니다.
    * `805`: 유효하지 않은 권한입니다.
    * `806`: 이미 존재하는 이메일입니다.
    * `807`: 존재하지 않는 회원 정보입니다.
    * `808`: 할 일이 존재하지 않습니다.

#### Exception Handler 오류 코드

* 9xx: 기본 메시지는 동적으로 변경하여 응답
    * `900`: 내부 오류가 발생했습니다. 확인 후 다시 시도해주세요.
    * `901`: 파라미터가 유효하지 않습니다.
    * `902`: 필수 파라미터가 누락되었습니다.
    * `903`: 파라미터 유효성 검사에 실패했습니다.
    * `904`: 파라미터 타입이 올바르지 않습니다.
    * `905`: 요청한 URL을 찾을 수 없습니다.
    * `906`: 지원하지 않는 메서드입니다.
    * `907`: 지원되지 않는 미디어 타입입니다.
    * `908`: 읽을 수 있는 요청 정보가 없습니다.

---

### Static Analysis Tools

#### Checkstyle

Checkstyle은 코드 스타일 유지를 위한 정적 코드 분석 도구입니다.

* **Version**: 10.20.0
* **Configuration**:
    * `config/checkstyle/google-checkstyle.xml`: Google 스타일 가이드를 따르는 Checkstyle 규칙을 정의한 설정 파일입니다.
    * `config/checkstyle/checkstyle-suppressions.xml`: 특정 경고나 규칙 위반을 억제하기 위한 설정 파일입니다.

#### PMD

PMD는 코드 품질 유지 및 코드 스타일 규칙 적용을 위한 정적 코드 분석 도구입니다.

* **Version**: 7.7.0
* **Configuration**:
    * `config/pmd/custom-ruleset.xml`: PMD 규칙을 정의한 설정 파일입니다.
