package com.example.boilerplate.sign.controller;

import com.example.boilerplate.common.reponse.BaseResponse;
import com.example.boilerplate.common.type.ApiStatus;
import com.example.boilerplate.sample.dto.MemberDto;
import com.example.boilerplate.sign.dto.SignDto;
import com.example.boilerplate.sign.service.SignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class SignController {

  private final SignService securityService;

  /**
   * 회원가입
   *
   * @param memberRequest MemberDto.Request
   * @return ResponseEntity
   */
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

  /**
   * 로그인
   *
   * @param signRequest SignDto.Request
   * @return ResponseEntity
   */
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

  /**
   * 관리자 조회 테스트
   *
   * @param user User - 헤더의 'Authorization' 키를 통해 획득된 토큰으로부터 인증 정보 추출
   * @return ResponseEntity
   */
  @GetMapping(value = "/admin", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity amdin(@AuthenticationPrincipal User user) {
    log.debug("user : {}", user);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(BaseResponse.builder()
            .statusCode(ApiStatus.OK.getCode())
            .data("admin")
            .build());
  }

  /**
   * 사용자 조회 테스트
   *
   * @param user User - 헤더의 'Authorization' 키를 통해 획득된 토큰으로부터 인증 정보 추출
   * @return ResponseEntity
   */
  @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity user(@AuthenticationPrincipal User user) {
    log.debug("user : {}", user);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(BaseResponse.builder()
            .statusCode(ApiStatus.OK.getCode())
            .data("user")
            .build());
  }
}
