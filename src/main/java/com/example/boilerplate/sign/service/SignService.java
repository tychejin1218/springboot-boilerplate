package com.example.boilerplate.sign.service;

import com.example.boilerplate.common.exception.ApiException;
import com.example.boilerplate.common.type.ApiStatus;
import com.example.boilerplate.config.security.JwtTokenProvider;
import com.example.boilerplate.domain.entity.MemberEntity;
import com.example.boilerplate.domain.repository.MemberRepository;
import com.example.boilerplate.sample.dto.MemberDto;
import com.example.boilerplate.sample.mapstruct.MemberMapStruct;
import com.example.boilerplate.sign.dto.SignDto;
import com.example.boilerplate.sign.dto.SignDto.Response;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SignService {

  private final MemberRepository memberRepository;
  private final MemberMapStruct memberMapStruct;
  private final JwtTokenProvider jwtTokenProvider;
  private final PasswordEncoder passwordEncoder;

  /**
   * 회원가입
   *
   * @param memberRequest 회원 요청 정보가 포함된 MemberDto.Request 객체
   * @return MemberDto.Response 회원 응답 정보가 포함된 객체
   */
  public MemberDto.Response signUp(MemberDto.Request memberRequest) {

    if (memberRepository.findByEmail(memberRequest.getEmail()).isPresent()) {
      throw new ApiException(ApiStatus.ALREADY_EXISTS_EMAIL);
    }

    MemberEntity member = memberMapStruct.toEntity(memberRequest);
    member.setRole("ROLE_USER");

    return memberMapStruct.toDto(memberRepository.save(member));
  }

  /**
   * 로그인
   *
   * @param signRequest 로그인 요청 정보가 포함된 SignDto.Request 객체
   * @return SignDto.Response 로그인 응답 정보가 포함된 객체
   */
  public SignDto.Response signIn(SignDto.Request signRequest) {

    MemberEntity member = memberRepository.findByEmail(signRequest.getEmail())
        .orElseThrow(() -> new ApiException(ApiStatus.INVALID_REQUEST));

    if (!passwordEncoder.matches(signRequest.getPassword(), member.getPassword())) {
      throw new ApiException(ApiStatus.INVALID_REQUEST);
    }

    return Response.builder()
        .token(jwtTokenProvider.createToken(
            member.getEmail(),
            Arrays.stream(member.getRole().split(",")).toList()))
        .build();
  }
}
