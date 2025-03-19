package com.example.boilerplate.sign.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

public class SignDto {

  @Schema(name = "signRequest", description = "로그인 요청")
  @Getter
  @SuperBuilder
  @AllArgsConstructor(staticName = "of")
  @NoArgsConstructor
  @ToString
  public static class Request {

    @Schema(description = "회원의 이메일", example = "honggildong@example.com")
    private String email;

    @Schema(description = "회원의 비밀번호", example = "password1!")
    private String password;
  }

  @Schema(name = "signResponse", description = "로그인 응답")
  @Getter
  @SuperBuilder
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString
  public static class Response {

    @Schema(description = "JWT 인증 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;
  }
}
