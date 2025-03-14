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

@SuppressWarnings("all")
@Tag(name = "할 일 API", description = "할 일 관련 API")
public interface TodoControllerApiDocs {

  @Operation(summary = "할 일 목록 조회")
  @ApiResponse(
      responseCode = "200",
      description = "할 일 목록 반환 성공",
      content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = List.class),
          examples = @ExampleObject(
              value = """
                  {
                    "statusCode": "200",
                    "message": "성공",
                    "data": [
                      {
                        "id": 1,
                        "title": "해야 할 일1",
                        "description": "자세한 설명",
                        "completed": false
                      },
                      {
                        "id": 2,
                        "title": "해야 할 일2",
                        "description": "자세한 설명",
                        "completed": true
                      }
                    ]
                  }
              """
          )
      )
  )
  BaseResponse<List<TodoDto.Response>> getTodos(@ModelAttribute TodoDto.Request todoRequest);

  @Operation(summary = "할 일 조회")
  @ApiResponse(
      responseCode = "200",
      description = "성공",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = TodoDto.Response.class),
          examples = @ExampleObject(
              value = """
                  {
                    "statusCode": "200",
                    "message": "성공",
                    "data": {
                      "id": 1,
                      "title": "해야 할 일1",
                      "description": "자세한 설명",
                      "completed": false
                    }
                  }
              """
          )
      )
  )
  @Parameter(name = "id", description = "할 일 ID", example = "1")
  BaseResponse<TodoDto.Response> getTodo(@PathVariable Long id);

  @Operation(summary = "할 일 저장")
  @ApiResponse(
      responseCode = "201",
      description = "할 일 저장 성공",
      content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = TodoDto.Response.class),
          examples = @ExampleObject(
              value = """
                  {
                    "statusCode": "201",
                    "message": "할 일 저장 성공",
                    "data": {
                      "id": 10,
                      "title": "새로운 할 일",
                      "description": "상세 내용",
                      "completed": false
                    }
                  }
              """
          )
      )
  )
  BaseResponse<TodoDto.Response> insertTodo(
      @RequestBody(
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = TodoDto.Request.class),
              examples = @ExampleObject(
                  value = """
                      {
                        "title": "새로운 할 일",
                        "description": "상세 내용",
                        "completed": false
                      }
                  """
              )
          )
      )
      TodoDto.Request todoRequest);

  @Operation(summary = "할 일 수정")
  @ApiResponse(
      responseCode = "200",
      description = "할 일 수정 성공",
      content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = TodoDto.Response.class),
          examples = @ExampleObject(
              value = """
                  {
                    "statusCode": "200",
                    "message": "할 일 수정 성공",
                    "data": {
                      "id": 10,
                      "title": "수정된 할 일",
                      "description": "수정된 설명",
                      "completed": true
                    }
                  }
              """
          )
      )
  )
  BaseResponse<TodoDto.Response> updateTodo(
      @RequestBody(
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = TodoDto.Request.class),
              examples = @ExampleObject(
                  value = """
                      {
                        "id": 10,
                        "title": "수정된 할 일",
                        "description": "수정된 설명",
                        "completed": true
                      }
                  """
              )
          )
      )
      TodoDto.Request todoRequest);
}
