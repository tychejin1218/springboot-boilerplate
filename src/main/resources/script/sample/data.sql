insert
into member (email, name, password, role)
values ('admin@example.com', '관리자', '$2a$10$Lwhekcxdlct9AJXV1JD3DO8/EIP7a73fqq80Lm9sbMbI.wQegv2ee',
        'ROLE_ADMIN'),
       ('user@example.com', '사용자', '$2a$10$Lwhekcxdlct9AJXV1JD3DO8/EIP7a73fqq80Lm9sbMbI.wQegv2ee',
        'ROLE_USER'),
       ('tester@example.com', '테스터', '$2a$10$Lwhekcxdlct9AJXV1JD3DO8/EIP7a73fqq80Lm9sbMbI.wQegv2ee',
        'ROLE_USER');

insert
into todo (member_id, title, description, completed)
values
    -- admin
    ('1', 'Spring Boot 시작하기', 'Spring Boot의 기본 개념과 설정 학습', false),
    ('1', 'Spring MVC 패턴 이해', 'Spring MVC와 컨트롤러 동작 원리 학습', true),
    ('1', 'JPA 기본 학습', 'Entity와 Repository 기초 학습', false),
    ('1', 'Spring Security 설정', '기본 인증 및 권한 제어 설정 학습', true),
    ('1', 'Spring Boot DevTools 활용', '개발 생산성을 위한 DevTools 설정', false),
    ('1', '리액티브 프로그래밍 학습', 'WebFlux로 리액티브 애플리케이션 작성', true),
    ('1', '자동화 테스트 작성', 'Spring Boot에서 TestRestTemplate 활용', false),
    ('1', 'Thymeleaf 템플릿 만들기', 'Thymeleaf를 활용한 HTML 페이지 구성', true),
    ('1', 'Spring Data JPA 연관관계 매핑', 'ManyToOne 및 연관관계 매핑 처리 학습', false),
    ('1', 'H2 데이터베이스 연결', 'Spring Boot에서 H2 설정 및 데이터 관리', true),
    ('1', '로깅 설정 및 활용', 'Lombok 및 Logback 설정 이해', false),
    ('1', 'Swagger 적용하기', 'SpringDoc으로 API 문서 자동 생성', true),
    ('1', '비동기 처리 구현', '@Async 애노테이션을 활용한 비동기 호출', false),
    ('1', 'Redis 캐시 적용', 'Spring Cache와 Redis 활용 방법 학습', true),
    ('1', '파일 업로드 및 다운로드', 'Spring MVC를 사용한 파일 처리 기능 개발', false),
    ('1', 'Spring Batch 처리', '대용량 데이터에 대한 배치 프로세스 작성', true),
    ('1', 'OAuth2 로그인 구현', 'Google OAuth2 인증 및 연동 설정', false),
    ('1', '스프링 프로파일 설정', '환경별 프로파일(local, dev) 구성 학습', true),
    ('1', 'Global Exception Handler 개발', 'ControllerAdvice를 통한 예외 처리', false),
    ('1', 'Spring WebSocket 학습', '실시간 메시징 구현 및 기본 설정', true),
    ('1', 'API 보안 강화', 'JWT 기반 인증을 적용해 REST API 보호', false),
    ('1', '테스트 코드 작성', 'JUnit5을 사용한 단위 테스트와 통합 테스트', true),
    ('1', 'Kafka 설정 학습', 'Spring Kafka에서 메시지 처리 구현', false),
    ('1', '스프링 부트 모니터링 및 Actuator 활용', '애플리케이션 상태 점검 및 EndPoint 설정', true),
    ('1', 'Event 기반 개발', 'Spring 어플리케이션 이벤트와 리스너 구현', false),
    ('1', 'MySQL 연동 설정', 'application.yml에서 MySQL 데이터베이스 설정', true),
    ('1', 'AOP 학습하기', 'Aspect-Oriented Programming을 적용하여 공통 관심사 처리', false),
    ('1', 'API Gateway 설정', 'Spring Cloud Gateway 학습 및 설정 적용', true),
    ('1', 'REST API 설계 및 테스트', 'Postman과 Rest Assured를 통한 API 테스트', false),

    -- user
    ('2', 'Spring Boot 기초 학습', 'Spring Boot 애플리케이션 생성 및 실행', false),
    ('2', 'Spring Dependency Injection 이해', '의존성 주입과 빈 주제 학습', true),
    ('2', 'JPA Entity 설계', 'Spring Data JPA로 Entity 매핑 설계', false),
    ('2', 'Spring Security OAuth2 적용', 'OAuth2 Client 구현 및 테스트', true),
    ('2', '개발 자동화 도구 활용', 'Spring Boot와 함께 Maven 활용', false),
    ('2', 'Spring WebFlux 이벤트 스트림', 'Flux와 Mono를 사용해 데이터 스트림 처리', true),
    ('2', '효율적인 테스트 환경 구축', 'MockMVC 및 TestRestTemplate 활용', false),
    ('2', 'Thymeleaf 폼 구성', '폼 데이터 바인딩 처리 및 검증', true),
    ('2', 'Spring JPA 다대다 연관관계 매핑', 'ManyToMany 연관관계 매핑 이해', false),
    ('2', 'H2 데이터베이스 콘솔 활용', 'H2 콘솔 접속 및 데이터 확인 설정', true),
    ('2', '로깅 필터 추가', '필터와 핸들러를 적용한 로그 관리', false),
    ('2', 'API 문서화 학습', 'Swagger UI를 활용한 API 엔드포인트 문서화', true),
    ('2', 'Executor 설정 및 커스터마이징', 'Spring @Async Task 설정 변경', false),
    ('2', 'Spring Cache로 데이터 캐싱 적용', '캐시 데이터 설정 및 만료 처리', true),
    ('2', '파일 업로드 구성 학습', '멀티파트 파일 업로드와 스토리지 연동', false),
    ('2', 'Spring Batch 마이그레이션 작성', 'CSV 및 데이터 마이그레이션 프로세스', true),
    ('2', '구글 OAuth2 로그인 연동', '구글 클라이언트와 통합 인증', false),
    ('2', '스프링 부트 프로파일 테스트', '다양한 프로파일 설정 및 애플리케이션 테스트 실행', true),
    ('2', '예외 메커니즘 설정', 'Spring에서의 Global Exception 처리 활용', false),
    ('2', 'WebSocket으로 실시간 시설 구현', 'STOMP 기반 브로드캐스팅 학습', true),
    ('2', 'API 보안 토큰 사용', 'CSRF와 JWT 토큰 조합 및 검증 처리', false),
    ('2', 'TDD를 활용한 서비스 설계', 'JUnit5와 Mockito 학습', true),
    ('2', 'Kafka로 비동기 메시지 처리', 'Spring Kafka의 기본 메시지 동작 학습', false),
    ('2', 'Actuator로 앱 상태 노출', '애플리케이션의 메트릭 검출 및 사용 설정', true),
    ('2', 'Event-listener로 확장 설계', 'Spring EventListeners 사용', false),
    ('2', 'MySQL 데이터베이스 연동', 'MySQL Connector 설정 및 동작 확인', true),
    ('2', 'AOP를 통한 비즈니스 단위 관리', 'Aspect와 어드바이스 활용 패턴', false),
    ('2', 'Spring Cloud Gateway 학습', 'Gateway를 통한 마이크로서비스 설정', true),
    ('2', 'RESTful API 설계', 'REST 아키텍쳐를 반영한 설계 학습', false),

    -- test
    ('3', 'JpaRepository 학습', 'JpaRepository 메서드를 통한 데이터 조작 학습', false),
    ('3', 'QueryDSL을 활용한 동적 쿼리', 'Spring Data와 QueryDSL 통합 활용', true),
    ('3', 'Spring Boot 애플리케이션 배포', 'Docker와 함께 프로젝트 컨테이너화하기', false),
    ('3', 'Spring Custom Annotation 제작', '커스텀 애노테이션 생성 및 활용', true),
    ('3', 'Spring Scheduler 학습', '@Scheduled를 사용하여 태스크 예약', false),
    ('3', 'Spring REST Docs 적용', 'API 문서 생성을 위한 Spring REST Docs 사용', true),
    ('3', 'Spring Integration 학습', '분산 환경에서의 시스템 통합 설정 학습', false),
    ('3', 'WebClient 학습', 'Spring에서 WebClient를 활용한 HTTP 호출', true),
    ('3', 'Spring Cloud의 Resilience4j 적용', '분산 시스템에서 장애 처리 구현', false),
    ('3', 'Apache Kafka 설정', 'Spring과 Kafka를 활용한 메시지 큐 구성', true);
