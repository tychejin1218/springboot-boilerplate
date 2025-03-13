package com.example.boilerplate.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

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
                .description("JWT Bearer Token 인증을 사용합니다. 'Bearer {JWT}'을 입력하세요."));

    return new OpenAPI()
        .addSecurityItem(new SecurityRequirement().addList(schemeName))
        .components(components)
        .addServersItem(new Server().url("/api"))
        .info(new Info()
            .title("Spring Boot Backend Boilerplate")
            .description("Spring Boot를 기반으로 한 Boilerplate 프로젝트의 REST API 정의")
            .version("1.0.0"));
  }
}
