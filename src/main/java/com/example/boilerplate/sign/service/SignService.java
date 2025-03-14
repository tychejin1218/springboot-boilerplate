package com.example.boilerplate.sign.service;

import com.example.boilerplate.common.exception.ApiException;
import com.example.boilerplate.common.type.ApiStatus;
import com.example.boilerplate.config.security.JwtTokenProvider;
import com.example.boilerplate.domain.entity.MemberEntity;
import com.example.boilerplate.domain.repository.MemberRepository;
import com.example.boilerplate.member.dto.MemberDto;
import com.example.boilerplate.sign.dto.SignDto;
import com.example.boilerplate.sign.dto.SignDto.Response;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
   * @param memberRequest 회원 요청 정보가 포함된 MemberDto.Request 객체
   * @return MemberDto.Response 회원 응답 정보가 포함된 객체
   */
  public MemberDto.Response signUp(MemberDto.Request memberRequest) {

    if (memberRepository.findByEmail(memberRequest.getEmail()).isPresent()) {
      throw new ApiException(ApiStatus.ALREADY_EXISTS_EMAIL);
    }

    MemberEntity memberEntity = modelMapper.map(memberRequest, MemberEntity.class);
    memberEntity.setPassword(passwordEncoder.encode(memberRequest.getPassword()));
    memberEntity.setRole("ROLE_USER");

    return modelMapper.map(memberRepository.save(memberEntity), MemberDto.Response.class);
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
