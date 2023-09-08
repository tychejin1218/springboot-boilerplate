package com.example.boilerplate.sign.service;

import com.example.boilerplate.common.exception.ApiException;
import com.example.boilerplate.common.type.ApiStatus;
import com.example.boilerplate.config.security.JwtTokenProvider;
import com.example.boilerplate.sample.domain.entity.Member;
import com.example.boilerplate.sample.domain.mapstruct.MemberMapStruct;
import com.example.boilerplate.sample.domain.repository.MemberRepository;
import com.example.boilerplate.sample.dto.MemberDto;
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

  public MemberDto.Response signUp(MemberDto.Request memberRequest) {
    if (memberRepository.findByEmail(memberRequest.getEmail()).isPresent()) {
      throw new ApiException(ApiStatus.ALREADY_EXISTS_EMAIL);
    }

    Member member = memberMapStruct.toEntity(memberRequest);
    member.setRole("ROLE_USER");

    return memberMapStruct.toDto(memberRepository.save(member));
  }

  public SignDto.Response signIn(SignDto.Request signRequest) {

    Member member = memberRepository.findByEmail(signRequest.getUsername())
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
