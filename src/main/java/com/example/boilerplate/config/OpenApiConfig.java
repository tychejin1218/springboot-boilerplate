package com.example.boilerplate.config;

import com.example.boilerplate.common.constants.Constants;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 설정 클래스
 *
 * <p>SpringDoc 및 Swagger를 사용하여 API 문서를 자동으로 생성하며,
 * JWT 인증을 위한 보안 스키마와 API를 그룹화하는 메서드들을 제공
 */
@Configuration
public class OpenApiConfig {

  /**
   * OpenAPI 설정을 생성하는 Bean
   *
   * <p>JWT 기반의 HTTP 인증 스키마를 구성하고, OpenAPI의 메타데이터를 포함하여 전체 API 문서를 설정
   *
   * @return OpenAPI 객체
   */
  @Bean
  public OpenAPI customOpenApi() {

    String schemeName = "JWTAuth";
    Components components = new Components()
        .addSecuritySchemes(schemeName,
            new SecurityScheme()
                .name(schemeName)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT Bearer Token을 사용한 인증 방식입니다. "
                    + "Authorization 헤더에 'Bearer your.jwt.token' 형식으로 입력해 주세요. "
                    + "예: Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."));

    return new OpenAPI()
        .addSecurityItem(new SecurityRequirement().addList(schemeName))
        .components(components)
        .addServersItem(new Server().url("/api"))
        .info(new Info()
            .title("Spring Boot Backend Boilerplate API")
            .description("이 프로젝트는 Spring Boot 기반의 백엔드 API Boilerplate로, "
                + "JWT 인증을 포함한 다양한 예제 API 엔드포인트를 제공합니다. "
                + "이를 통해 기본적인 사용자 인증 및 샘플 API의 활용 방법을 확인할 수 있습니다.")
            .version("1.0.0"));
  }

  /**
   * 인증 관련 API 그룹을 생성하는 Bean
   *
   * <p>"/sign/**" 경로에 있는 API를 그룹화
   *
   * @return GroupedOpenApi 객체
   */
  @Bean
  public GroupedOpenApi signApi() {
    return createGroupedApi("auth-api", "/sign/**");
  }

  /**
   * 샘플 API 그룹을 생성하는 Bean
   *
   * <p>"/sample/**" 경로에 있는 API를 그룹화
   *
   * @return GroupedOpenApi 객체
   */
  @Bean
  public GroupedOpenApi sampleApi() {
    return createGroupedApi("sample-api", "/sample/**");
  }

  /**
   * GroupedOpenApi 객체를 생성하는 유틸리티 메서드
   *
   * <p>API 그룹 이름과 경로를 기반으로 GroupedOpenApi 객체를 반환
   *
   * @param groupName 그룹 이름
   * @param path      매칭할 경로
   * @return GroupedOpenApi 객체
   */
  private GroupedOpenApi createGroupedApi(String groupName, String path) {
    return GroupedOpenApi.builder()
        .group(groupName)
        .pathsToMatch(path)
        .packagesToScan(Constants.BASE_PACKAGE)
        .build();
  }
}
