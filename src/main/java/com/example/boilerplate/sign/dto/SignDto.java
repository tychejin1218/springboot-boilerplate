package com.example.boilerplate.sign.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

public class SignDto {

  @Getter
  @Setter
  @SuperBuilder
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString
  public static class Request {

    private String email;
    private String password;
  }

  @Getter
  @Setter
  @SuperBuilder
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString
  public static class Response {

    private String token;
  }
}
