package com.example.boilerplate.sign.controller;

import com.example.boilerplate.common.response.BaseResponse;
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
public class SignController implements SignControllerDocs {

  private final SignService signService;

  /**
   * 회원가입
   *
   * @param signUpRequest 회원가입 요청 정보 (이메일, 이름, 비밀번호)
   * @return 회원 정보를 반환
   */
  @PostMapping("/sign/signup")
  @Override
  public BaseResponse<SignDto.SignUpResponse> signUp(
      @RequestBody SignDto.SignUpRequest signUpRequest) {
    return BaseResponse.ok(signService.signUp(signUpRequest));
  }

  /**
   * 로그인
   *
   * @param signInRequest 로그인 요청 정보 (이메일, 비밀번호)
   * @return JWT 인증 토큰을 반환
   */
  @PostMapping("/sign/signin")
  @Override
  public BaseResponse<SignDto.SignInResponse> signIn(
      @RequestBody SignDto.SignInRequest signInRequest) {
    return BaseResponse.ok(signService.signIn(signInRequest));
  }

  /**
   * 관리자 조회 테스트
   *
   * @param user User - 헤더의 'Authorization' 키를 통해 획득된 토큰으로부터 인증 정보 추출
   * @return 관리자 정보 반환
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
   * @return 사용자 정보 반환
   */
  @Hidden
  @GetMapping("/user")
  public BaseResponse<String> user(@AuthenticationPrincipal User user) {
    log.debug("user : {}", user);
    return BaseResponse.ok("user");
  }
}
