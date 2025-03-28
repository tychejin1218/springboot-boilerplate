package com.example.boilerplate.sign.controller;

import com.example.boilerplate.common.response.BaseResponse;
import com.example.boilerplate.member.dto.MemberDto;
import com.example.boilerplate.sign.dto.SignDto;
import com.example.boilerplate.sign.service.SignService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class SignController implements SignControllerApiDocs {

  private final SignService signService;

  /**
   * 회원가입
   *
   * @param memberRequest MemberDto.Request
   * @return ResponseEntity
   */
  @PostMapping("/sign/signup")
  @Override
  public BaseResponse<MemberDto.Response> signUp(
      @RequestBody MemberDto.Request memberRequest) {
    return BaseResponse.ok(signService.signUp(memberRequest));
  }

  /**
   * 로그인
   *
   * @param signRequest SignDto.Request
   * @return ResponseEntity
   */
  @PostMapping("/sign/signin")
  @Override
  public BaseResponse<SignDto.Response> signIn(
      @RequestBody SignDto.Request signRequest) {
    return BaseResponse.ok(signService.signIn(signRequest));
  }

  /**
   * 관리자 조회 테스트
   *
   * @param user User - 헤더의 'Authorization' 키를 통해 획득된 토큰으로부터 인증 정보 추출
   * @return ResponseEntity
   */
  @Hidden
  @GetMapping("/admin")
  public BaseResponse<String> amdin(@AuthenticationPrincipal User user) {
    log.debug("user : {}", user);
    return BaseResponse.ok("admin");
  }

  /**
   * 사용자 조회 테스트
   *
   * @param user User - 헤더의 'Authorization' 키를 통해 획득된 토큰으로부터 인증 정보 추출
   * @return ResponseEntity
   */
  @Hidden
  @GetMapping("/user")
  public BaseResponse<String> user(@AuthenticationPrincipal User user) {
    log.debug("user : {}", user);
    return BaseResponse.ok("user");
  }
}
