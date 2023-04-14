package com.example.boilerplate.sample.dto;

import com.example.boilerplate.common.dto.PageDto;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

public class TodoDto {

  @Getter
  @Setter
  @SuperBuilder
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString(callSuper = true)
  public static class Request extends PageDto {

    private Long id;
    private Long memberId;
    private String title;
    private String description;
    private Boolean completed;
  }

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString
  public static class Response {

    private Long id;
    private String title;
    private String description;
    private Boolean completed;
    private Member member;
  }

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString
  public static class Member {

    private Long id;
    private String name;
    private String email;
  }

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString
  public static class Exception {

    private Long id;
    private Long memberId;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    private Boolean completed;
  }
}
