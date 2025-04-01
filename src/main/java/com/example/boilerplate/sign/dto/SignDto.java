package com.example.boilerplate.sign.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class SignDto {

  @Schema(name = "signUpRequest", description = "회원가입 요청")
  @Getter
  @Builder
  @AllArgsConstructor(staticName = "of")
  @NoArgsConstructor
  @ToString
  public static class SignUpRequest {

    @Schema(description = "회원 이메일 (유일값)", example = "tester@example.com")
    private String email;

    @Schema(description = "회원 이름", example = "테스터")
    private String name;

    @Schema(description = "회원 비밀번호", example = "password1!")
    private String password;
  }

  @Schema(name = "signUpResponse", description = "회원가입 응답")
  @Getter
  @Builder
  @AllArgsConstructor(staticName = "of")
  @NoArgsConstructor
  @ToString
  public static class SignUpResponse {

    @Schema(description = "회원 ID (자동 증가값)", example = "3")
    private String id;

    @Schema(description = "회원 이메일 (유일값)", example = "tester@example.com")
    private String email;

    @Schema(description = "회원 이름", example = "테스터")
    private String name;

    @Schema(description = "회원 역할", example = "password1!")
    private String role;
  }


  @Schema(name = "signInRequest", description = "로그인 요청")
  @Getter
  @Builder
  @AllArgsConstructor(staticName = "of")
  @NoArgsConstructor
  @ToString
  public static class SignInRequest {

    @Schema(description = "회원 이메일 (유일값)", example = "tester@example.com")
    private String email;

    @Schema(description = "회원 비밀번호", example = "password1!")
    private String password;
  }

  @Schema(name = "signInResponse", description = "로그인 응답")
  @Getter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString
  public static class SignInResponse {

    @Schema(description = "JWT 인증 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;
  }
}
