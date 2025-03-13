package com.example.boilerplate.sign.controller;

import com.example.boilerplate.common.response.BaseResponse;
import com.example.boilerplate.sample.dto.MemberDto;
import com.example.boilerplate.sign.dto.SignDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@SuppressWarnings("all")
@Tag(name = "Sign API", description = "회원 인증 및 로그인 API")
public interface SignControllerApiDocs {

  @Operation(summary = "회원가입")
  @ApiResponse(
      responseCode = "200",
      description = "성공",
      content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = MemberDto.Response.class),
          examples = @ExampleObject(
              value = """
                  {"statusCode": "200","message": "성공","data": {"id": 1000,"password": "$2a$10$chYb170Nrq7K5ad3hmBBMuxb6tJjSmmkgi9nEUmWWmQPd2EwVqyMe","name": "test10","email": "test10@gmail.com","todos": []}}
                  """
          )
      )
  )
  BaseResponse<MemberDto.Response> signUp(
      @RequestBody(
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = MemberDto.Request.class),
              examples = @ExampleObject(
                  value = """
                      {"name": "test10","password": "password1!","email": "test10@gmail.com"}
                      """
              )
          )
      )
      MemberDto.Request memberRequest);

  @Operation(summary = "로그인")
  @ApiResponse(
      responseCode = "200",
      description = "성공",
      content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = SignDto.Response.class),
          examples = @ExampleObject(
              value = """
                  {"statusCode": "200","message": "성공","data": {"token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0MTBAZ21haWwuY29tIiwicm9sZXMiOlsiUk9MRV9VU0VSIl0sImlhdCI6MTc0MTg1MDY0MCwiZXhwIjoxNzQxODUwOTQwfQ.w0911tnZBeyGQpJcb6xUeVrZHKstSDe9PAd5a1ffqnI"}}
                  """
          )
      )
  )
  BaseResponse<SignDto.Response> signIn(
      @RequestBody(
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = SignDto.Request.class),
              examples = @ExampleObject(
                  value = """
                      {"email": "test10@gmail.com","password": "password1!"}
                      """
              )
          )
      )
      SignDto.Request signRequest);
}
