package com.example.boilerplate.member.controller;

import com.example.boilerplate.common.response.BaseResponse;
import com.example.boilerplate.member.dto.MemberDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

@SuppressWarnings("all")
@Tag(name = "회원 API", description = "회원 관련 API")
public interface MemberControllerDocs {

  @Operation(summary = "회원 목록 조회")
  @ApiResponse(
      responseCode = "200",
      description = "성공",
      content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = Page.class),
          examples = @ExampleObject(
              value = """
                  {"statusCode": "200", "message": "성공", "data": {"content": [{"id": 1, "password": "$2a$10$zXtqykQh3C3jwIySCUy4ueIK/jdWPF3ELPTt0JidbTOgtY79rCXlW", "name": "홍길동", "email": "hong@naver.com", "todos": []},{"id": 2, "password": "$2a$10$zXtqykQh3C3jwIySCUy4ueIK/jdWPF3ELPTt0JidbTOgtY79rCXlW", "name": "김철수", "email": "kim@test.com", "todos": []}],"pageable": {"pageNumber": 0, "pageSize": 10},"totalElements": 2, "totalPages": 1}}
              """
          )
      )
  )
  BaseResponse<Page<MemberDto.Response>> getMemberList(@ParameterObject @ModelAttribute MemberDto.Request memberRequest);


  @Operation(summary = "회원 조회")
  @ApiResponse(
      responseCode = "200",
      description = "성공",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = MemberDto.Response.class),
          examples = @ExampleObject(
              value = """
                  {
                    "statusCode": "200",
                    "message": "성공",
                    "data": {
                      "id": 1,
                      "name": "홍길동",
                      "email": "hong@test.com"
                    }
                  }
              """
          )
      )
  )
  @Parameter(name = "id", description = "회원 ID", example = "1")
  BaseResponse<MemberDto.Response> getMember(@PathVariable Long id);

  @Operation(summary = "회원 저장")
  @ApiResponse(
      responseCode = "201",
      description = "성공",
      content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = MemberDto.Response.class),
          examples = @ExampleObject(
              value = """
                  {
                    "statusCode": "201",
                    "message": "회원 저장 성공",
                    "data": {
                      "id": 10,
                      "name": "이수민",
                      "email": "lee@test.com"
                    }
                  }
              """
          )
      )
  )
  BaseResponse<MemberDto.Response> insertMember(
      @RequestBody(
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = MemberDto.InsertRequest.class),
              examples = @ExampleObject(
                  value = """
                      {
                        "name": "이수민",
                        "password": "securePass123!",
                        "email": "lee@test.com"
                      }
                  """
              )
          )
      )
      MemberDto.InsertRequest insertMemberRequest);

  @Operation(summary = "회원 수정")
  @ApiResponse(
      responseCode = "200",
      description = "회원 정보 수정 성공",
      content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = MemberDto.Response.class),
          examples = @ExampleObject(
              value = """
                  {
                    "statusCode": "200",
                    "message": "회원 정보 수정 성공",
                    "data": {
                      "id": 10,
                      "name": "이수민",
                      "email": "newemail@test.com"
                    }
                  }
              """
          )
      )
  )
  BaseResponse<MemberDto.Response> updateMember(
      @RequestBody(
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = MemberDto.UpdateRequest.class),
              examples = @ExampleObject(
                  value = """
                      {
                        "id": 10,
                        "name": "이수민",
                        "password": "updatedPass123!",
                        "email": "newemail@test.com"
                      }
                  """
              )
          )
      )
      MemberDto.UpdateRequest updateMemberRequest);
}
