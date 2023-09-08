package com.example.boilerplate.sign.controller;

import com.example.boilerplate.common.type.ApiStatus;
import com.example.boilerplate.sample.dto.MemberDto;
import com.example.boilerplate.sign.service.SignService;
import com.example.boilerplate.sign.dto.SignDto;
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
public class SignController {

  private final SignService securityService;

  @PostMapping(
      value = "/sign/signup",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity signUp(@RequestBody MemberDto.Request memberRequest) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(BaseResponse.builder()
            .statusCode(ApiStatus.OK.getCode())
            .data(securityService.signUp(memberRequest))
            .build());
  }

  @PostMapping(
      value = "/sign/signin",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity signIn(@RequestBody SignDto.Request signRequest) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(BaseResponse.builder()
            .statusCode(ApiStatus.OK.getCode())
            .data(securityService.signIn(signRequest))
            .build());
  }
}
