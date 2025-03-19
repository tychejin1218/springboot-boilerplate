package com.example.boilerplate.todo.contoller;

import com.example.boilerplate.common.response.BaseResponse;
import com.example.boilerplate.todo.dto.TodoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@SuppressWarnings("all")
@Tag(name = "할 일 API", description = "할 일 관련 API")
public interface TodoControllerDocs {

  @Operation(summary = "할 일 목록 조회")
  @ApiResponse(
      responseCode = "200",
      description = "할 일 목록 반환 성공",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = TodoDto.Response.class),
          examples = @ExampleObject(
              value = """
                  {"statusCode": "200", "message": "성공", "data": [{"id": 2, "title": "Spring Boot DevTools 설정", "description": "개발 환경에서 자동 리로드 설정", "completed": true}]}
                  """
          )
      )
  )
  @Parameter(name = "title", description = "제목", example = "spring")
  @Parameter(name = "description", description = "내용", example = "설정")
  @Parameter(name = "completed", description = "완료 여부", example = "true")
  BaseResponse<List<TodoDto.Response>> getTodos(
      @RequestParam(value = "title", required = false) String title,
      @RequestParam(value = "description", required = false) String description,
      @RequestParam(value = "completed", required = false) Boolean completed);

  @Operation(summary = "특정 할 일 조회")
  @ApiResponse(
      responseCode = "200",
      description = "성공",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = TodoDto.Response.class),
          examples = @ExampleObject(
              value = """
                  {"statusCode": "200", "message": "성공", "data": {"id": 1, "title": "Spring Security 학습", "description": "JWT 기반 인증 및 권한 부여 학습하기", "completed": true}}
                  """
          )
      )
  )
  @Parameter(name = "id", description = "할 일 아이디", example = "1")
  BaseResponse<TodoDto.Response> getTodo(@PathVariable Long id);

  @Operation(summary = "할 일 추가")
  @ApiResponse(
      responseCode = "200",
      description = "성공",
      content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = TodoDto.Response.class),
          examples = @ExampleObject(
              value = """
                      {"statusCode": "200", "message": "성공", "data": {"id": 1, "title": "Spring Boot 기초 학습", "description": "Spring Boot 기본 개념, 설정 및 간단한 API 구현", "completed": false}}
                  """
          )
      )
  )
  BaseResponse<TodoDto.Response> insertTodo(
      @RequestBody(
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = TodoDto.InsertRequest.class),
              examples = @ExampleObject(
                  value = """
                          {"title": "Spring Boot 기초 학습", "description": "Spring Boot 기본 개념, 설정 및 간단한 API 구현"}
                      """
              )
          )
      )
      TodoDto.InsertRequest insertTodoRequest);

  @Operation(summary = "할 일 수정")
  @ApiResponse(
      responseCode = "200",
      description = "할 일 수정 성공",
      content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = TodoDto.Response.class),
          examples = @ExampleObject(
              value = """
                      {"statusCode": "200", "message": "성공", "data": {"id": 1, "title": "Spring Boot 심화 학습", "description": "Spring Boot와 JPA를 활용한 프로젝트 구성", "completed": true}}
                  """
          )
      )
  )
  BaseResponse<TodoDto.Response> updateTodo(
      @RequestBody(
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = TodoDto.UpdateRequest.class),
              examples = @ExampleObject(
                  value = """
                          {"id": 1, "title": "Spring Boot 심화 학습", "description": "Spring Boot와 JPA를 활용한 프로젝트 구성", "completed": true}
                      """
              )
          )
      )

      TodoDto.UpdateRequest updateTodoRequest);

  @Operation(summary = "할 일 삭제")
  @ApiResponse(
      responseCode = "200",
      description = "할 일 삭제 성공",
      content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = TodoDto.Response.class),
          examples = @ExampleObject(
              value = """
                      {"statusCode": "200", "message": "성공", "data": {"id": 1, "title": "Spring Boot 심화 학습", "description": "Spring Boot와 JPA를 활용한 프로젝트 구성", "completed": true}}
                  """
          )
      )
  )
  @Parameter(name = "id", description = "할 일 ID", example = "1")
  BaseResponse<TodoDto.Response> deleteTodo(@PathVariable Long id);
}
