package com.example.boilerplate.sample.dto;

import com.example.boilerplate.common.dto.PageDto;
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
  @ToString(callSuper = true)
  public static class Request extends PageDto {

    private Long id;
    private String password;
    private String name;
    private String email;
  }

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString
  public static class Response {

    private Long id;
    private String password;
    private String name;
    private String email;

    List<Todo> todos;
  }

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString
  public static class Todo {

    private Long id;
    private String title;
    private String description;
    private Boolean completed;
  }
}
