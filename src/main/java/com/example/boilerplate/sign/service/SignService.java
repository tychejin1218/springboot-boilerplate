package com.example.boilerplate.sign.service;

import com.example.boilerplate.common.exception.ApiException;
import com.example.boilerplate.common.type.ApiStatus;
import com.example.boilerplate.config.security.JwtTokenProvider;
import com.example.boilerplate.domain.entity.MemberEntity;
import com.example.boilerplate.domain.repository.MemberRepository;
import com.example.boilerplate.sign.dto.SignDto;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SignService {

  private final MemberRepository memberRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final PasswordEncoder passwordEncoder;
  private final ModelMapper modelMapper;

  /**
   * 회원가입
   *
   * @param signUpRequest 회원가입 요청 정보 (이메일, 이름, 비밀번호)
   * @return 회원 정보를 반환
   */
  public SignDto.SignUpResponse signUp(SignDto.SignUpRequest signUpRequest) {

    if (memberRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
      throw new ApiException(ApiStatus.ALREADY_EXISTS_EMAIL);
    }

    MemberEntity memberEntity = modelMapper.map(signUpRequest, MemberEntity.class);
    memberEntity.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
    memberEntity.setRole("ROLE_USER");

    return modelMapper.map(memberRepository.save(memberEntity), SignDto.SignUpResponse.class);
  }

  /**
   * 로그인
   *
   * @param signInRequest 로그인 요청 정보 (이메일, 비밀번호)
   * @return JWT 인증 토큰을 반환
   */
  public SignDto.SignInResponse signIn(SignDto.SignInRequest signInRequest) {

    MemberEntity member = memberRepository.findByEmail(signInRequest.getEmail())
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, ApiStatus.MEMBER_NOT_FOUND));

    if (!passwordEncoder.matches(signInRequest.getPassword(), member.getPassword())) {
      throw new ApiException(HttpStatus.BAD_REQUEST, ApiStatus.INVALID_REQUEST);
    }

    return SignDto.SignInResponse.builder()
        .token(jwtTokenProvider.createToken(
            member.getEmail(),
            Arrays.stream(member.getRole().split(",")).toList()))
        .build();
  }
}
