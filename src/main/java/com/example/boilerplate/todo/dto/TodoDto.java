package com.example.boilerplate.todo.dto;

import com.example.boilerplate.common.dto.PageDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
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

    private String title;
    private String description;
    private Boolean completed;

    public static TodoDto.PageRequest of(String title, String description,
        Boolean completed, int page, int size) {
      return TodoDto.PageRequest.builder()
          .title(title)
          .description(description)
          .completed(completed)
          .page(page)
          .size(size)
          .build();
    }

    public static TodoDto.PageRequest of(String title, String description,
        Boolean completed, int page, int size, List<String> sorts) {
      return TodoDto.PageRequest.builder()
          .title(title)
          .description(description)
          .completed(completed)
          .page(page)
          .size(size)
          .sorts(sorts)
          .build();
    }
  }

  @Getter
  @Builder
  @AllArgsConstructor(staticName = "of")
  @NoArgsConstructor
  @ToString
  public static class DynamicRequest {

    private Long id;
    private Boolean completed;
  }

  @Schema(description = "할 일 추가 요청 DTO")
  @Getter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString
  public static class InsertRequest {

    @Schema(description = "회원 아이디", example = "1L")
    private Long memberId;

    @Schema(description = "할 일 제목", example = "Spring Boot 기초 학습")
    private String title;

    @Schema(description = "할 일 내용", example = "Spring Boot 기본 개념, 설정 및 간단한 API 구현")
    private String description;

    @Schema(description = "완료 여부 (기본값: false)")
    @Builder.Default
    private Boolean completed = false;

    public static TodoDto.InsertRequest of(long memberId, String title, String description) {
      return InsertRequest.builder()
          .memberId(memberId)
          .title(title)
          .description(description)
          .build();
    }

    public static TodoDto.InsertRequest of(long memberId, String title, String description,
        boolean completed) {
      return InsertRequest.builder()
          .memberId(memberId)
          .title(title)
          .description(description)
          .completed(completed)
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
  @Builder
  @AllArgsConstructor(staticName = "of")
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
  @Builder
  @AllArgsConstructor(staticName = "of")
  @NoArgsConstructor
  @ToString
  public static class Member {

    @Schema(name = "id", example = "1", description = "회원 아이디", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    @Schema(name = "name", example = "테스터", description = "회원 이름", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String name;

    @Schema(name = "email", example = "tester@example.com", description = "회원 이메일", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String email;
  }
}
