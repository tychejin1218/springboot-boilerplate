package com.example.boilerplate.todo.dto;

import com.example.boilerplate.common.dto.PageDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

public class TodoDto {

  @Getter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString
  public static class Request {

    private Long id;
    private String title;
    private String description;
    private Boolean completed;

    public static TodoDto.Request of(Long id, String title, String description, Boolean completed) {
      return TodoDto.Request.builder()
          .id(id)
          .title(title)
          .description(description)
          .completed(completed)
          .build();
    }

    public static TodoDto.Request of(String title, String description, Boolean completed) {
      return TodoDto.Request.builder()
          .title(title)
          .description(description)
          .completed(completed)
          .build();
    }
  }

  @Getter
  @Setter
  @SuperBuilder
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString(callSuper = true)
  public static class PageRequest extends PageDto {

    private Long id;
    private Long memberId;
    private String title;
    private String description;
    private Boolean completed;
  }

  @Schema(description = "할 일 추가 요청 DTO")
  @Getter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString
  public static class InsertRequest {

    @Schema(description = "할 일 제목", example = "Spring Boot 기초 학습")
    private String title;

    @Schema(description = "할 일 내용", example = "Spring Boot 기본 개념, 설정 및 간단한 API 구현")
    private String description;

    @Schema(description = "완료 여부 (기본값: false)")
    @Builder.Default
    private Boolean completed = false;

    public static TodoDto.InsertRequest of(String title, String description) {
      return TodoDto.InsertRequest.builder()
          .title(title)
          .description(description)
          .build();
    }
  }

  @Schema(description = "할 일 수정 요청 DTO")
  @Getter
  @Builder
  @AllArgsConstructor(staticName = "of")
  @NoArgsConstructor
  @ToString
  public static class UpdateRequest {

    @Schema(description = "수정할 할 일의 ID", example = "1")
    private Long id;

    @Schema(description = "수정할 제목", example = "Spring Boot 심화 학습")
    private String title;

    @Schema(description = "수정할 내용", example = "Spring Boot와 JPA를 활용한 프로젝트 구성")
    private String description;

    @Schema(description = "완료 여부", example = "true")
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
    private TodoDto.Member member;
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
}
