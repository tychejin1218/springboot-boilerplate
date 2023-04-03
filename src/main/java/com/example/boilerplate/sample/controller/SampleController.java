package com.example.boilerplate.sample.controller;

import com.example.boilerplate.common.type.ApiStatus;
import com.example.boilerplate.sample.dto.MemberDto;
import com.example.boilerplate.sample.service.SampleService;
import com.example.boilerplate.web.reponse.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class SampleController {

  private final SampleService sampleService;

  /**
   * Member 목록 조회
   */
  @PostMapping(
      value = "/api/sample/members",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity getMembers(
      @RequestBody MemberDto.Request memberRequest) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(BaseResponse.builder()
            .statusCode(ApiStatus.OK.getCode())
            .data(sampleService.getMembers(memberRequest))
            .build());
  }

  /**
   * Member 상세 조회
   */
  @PostMapping(
      value = "/api/sample/member",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity getMember(
      @RequestBody MemberDto.Request memberRequest) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(BaseResponse.builder()
            .statusCode(ApiStatus.OK.getCode())
            .data(sampleService.getMember(memberRequest))
            .build());
  }

  /**
   * Member 저장
   */
  @PostMapping(
      value = "/api/sample/insert/member",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity insertMember(
      @RequestBody MemberDto.Request memberRequest) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(BaseResponse.builder()
            .statusCode(ApiStatus.OK.getCode())
            .data(sampleService.getMember(memberRequest))
            .build());
  }
}
