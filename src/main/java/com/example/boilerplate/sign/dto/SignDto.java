package com.example.boilerplate.sign.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

public class SignDto {

  @Schema(description = "로그인 요청 DTO")
  @Getter
  @Setter
  @SuperBuilder
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString
  public static class Request {

    @Schema(description = "사용자 이메일", example = "hong@naver.com")
    private String email;

    @Schema(description = "사용자 비밀번호", example = "password1!")
    private String password;
  }

  @Schema(description = "로그인 응답 DTO")
  @Getter
  @Setter
  @SuperBuilder
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString
  public static class Response {

    @Schema(description = "JWT 인증 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;
  }
}
