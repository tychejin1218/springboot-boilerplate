package com.example.boilerplate.member.dto;

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

public class MemberDto {

  @Getter
  @Setter
  @SuperBuilder
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString
  public static class Request {

    private Long id;
    private String password;
    private String name;
    private String email;

    public static MemberDto.Request of(Long id) {
      return MemberDto.Request.builder()
          .id(id)
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

    private String name;
    private String email;

    public static MemberDto.PageRequest of(String name, String email) {
      return MemberDto.PageRequest.builder()
          .name(name)
          .email(email)
          .build();
    }

    public static MemberDto.PageRequest of(String name, String email,
        int page, int size, List<String> sorts) {
      return MemberDto.PageRequest.builder()
          .name(name)
          .email(email)
          .page(page)
          .size(size)
          .sorts(sorts)
          .build();
    }
  }

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString
  public static class Response {

    @Schema(name = "id", example = "1", description = "회원 아이디", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    @Schema(name = "password", example = "$2a$10$Lwhekcxdlct9AJXV1JD3DO8/EIP7a73fqq80Lm9sbMbI.wQegv2ee", description = "회원의 암호화된 비밀번호", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String password;

    @Schema(name = "name", example = "테스터", description = "회원 이름", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String name;

    @Schema(name = "email", example = "tester@example.com", description = "회원 이메일", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String email;

    @Schema(description = "회원의 할 일 목록")
    List<MemberDto.Todo> todos;
  }

  @Schema(description = "할 일 정보 DTO")
  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString
  public static class Todo {

    @Schema(name = "id", example = "10", description = "할 일의 아이디")
    private Long id;

    @Schema(name = "title", example = "할 일 - 제목", description = "할 일의 제목")
    private String title;

    @Schema(name = "description", example = "할 일 - 내용", description = "할 일의 상세 설명")
    private String description;

    @Schema(name = "completed", example = "false", description = "할 일의 완료 여부")
    private Boolean completed;
  }
}
