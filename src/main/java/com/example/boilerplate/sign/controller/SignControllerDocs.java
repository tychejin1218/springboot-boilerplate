package com.example.boilerplate.sign.controller;

import com.example.boilerplate.common.response.BaseResponse;
import com.example.boilerplate.sign.dto.SignDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@SuppressWarnings("all")
@Tag(name = "인증 API", description = "인증 관련 API")
public interface SignControllerDocs {

  @Operation(summary = "회원가입")
  @ApiResponse(
      responseCode = "200",
      description = "성공",
      content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = SignDto.SignUpResponse.class),
          examples = @ExampleObject(
              value = """
                  {"statusCode":"200","message":"성공","data":{"id":"3","email":"tester@example.com","name":"테스터","role":"ROLE_USER"}}
                  """
          )
      )
  )
  BaseResponse<SignDto.SignUpResponse> signUp(
      @RequestBody(
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = SignDto.SignUpRequest.class),
              examples = @ExampleObject(
                  value = """
                      {"email":"tester@example.com","name":"테스터","password":"password1!"}
                      """
              )
          )
      )
      SignDto.SignUpRequest signUpRequest);

  @Operation(summary = "로그인")
  @ApiResponse(
      responseCode = "200",
      description = "성공",
      content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = SignDto.SignInResponse.class),
          examples = @ExampleObject(
              value = """
                  {"statusCode": "200","message": "성공","data": {"token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0MTBAZ21haWwuY29tIiwicm9sZXMiOlsiUk9MRV9VU0VSIl0sImlhdCI6MTc0MTg1MDY0MCwiZXhwIjoxNzQxODUwOTQwfQ.w0911tnZBeyGQpJcb6xUeVrZHKstSDe9PAd5a1ffqnI"}}
                  """
          )
      )
  )
  BaseResponse<SignDto.SignInResponse> signIn(
      @RequestBody(
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = SignDto.SignInRequest.class),
              examples = @ExampleObject(
                  value = """
                      {"email": "tester@example.com","password": "password1!"}
                      """
              )
          )
      )
      SignDto.SignInRequest signInRequest);
}
