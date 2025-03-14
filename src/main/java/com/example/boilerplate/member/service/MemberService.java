package com.example.boilerplate.member.service;

import com.example.boilerplate.common.component.RedisComponent;
import com.example.boilerplate.common.exception.ApiException;
import com.example.boilerplate.common.type.ApiStatus;
import com.example.boilerplate.domain.entity.MemberEntity;
import com.example.boilerplate.domain.repository.MemberRepository;
import com.example.boilerplate.member.dto.MemberDto;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

  private final MemberRepository memberRepository;
  private final RedisComponent redisComponent;
  private final ModelMapper modelMapper;

  /**
   * 회원 목록 조회
   *
   * @param memberRequest 검색 조건이 포함된 MemberDto.Request 객체
   * @return Page&lt;MemberDto.Response&gt; 페이징 처리된 회원 목록
   */
  @Transactional(readOnly = true)
  public Page<MemberDto.Response> getMemberList(MemberDto.Request memberRequest) {
    Page<MemberEntity> memberPage =
        memberRepository.findAllByNameContainsAndEmailContains(
            memberRequest.getName(),
            memberRequest.getEmail(),
            memberRequest.pageRequest());
    return memberPage.map(member -> modelMapper.map(member, MemberDto.Response.class));
  }

  /**
   * 회원 ID를 통해 특정 회원의 정보를 조회
   *
   * @param memberRequest ID 정보가 포함된 MemberDto.Request 객체
   * @return MemberDto.Response 조회된 회원의 정보
   */
  @Transactional(readOnly = true)
  public MemberDto.Response getMember(MemberDto.Request memberRequest) {

    // Redis에 사용할 키 생성
    String redisKey = String.format("SAMPLE:MEMBER:%s", memberRequest.getId());

    // Redis에서 회원 정보를 조회
    MemberDto.Response memberResponse = redisComponent.getObjectValue(
        redisKey, new TypeReference<>() {
        });
    log.debug("Redis redisKey : {}, memberResponse : {}", redisKey, memberResponse);

    if (ObjectUtils.isEmpty(memberResponse)) {

      // 회원 정보를 조회
      MemberEntity member = memberRepository.findById(memberRequest.getId())
          .orElseThrow(() -> new ApiException(ApiStatus.MEMBER_NOT_FOUND));
      memberResponse = modelMapper.map(member, MemberDto.Response.class);
      log.debug("MySQL memberId : {}, memberResponse : {}", memberRequest.getId(), memberResponse);

      // Redis에 회원 정보를 저장
      redisComponent.setObjectValue(redisKey, memberResponse, 10L, TimeUnit.MINUTES);
    }

    return memberResponse;
  }

  /**
   * 회원 저장
   *
   * @param memberRequest 저장할 회원 정보가 포함된 MemberDto.Request 객체
   * @return MemberDto.Response 저장된 회원의 정보
   */
  @Transactional
  public MemberDto.Response insertMember(MemberDto.Request memberRequest) {
    MemberEntity memberEntity = modelMapper.map(memberRequest, MemberEntity.class);
    return modelMapper.map(memberRepository.save(memberEntity), MemberDto.Response.class);
  }

  /**
   * 회원 수정
   *
   * @param memberRequest 수정할 정보가 포함된 MemberDto.Request 객체
   * @return MemberDto.Response 수정된 회원의 정보
   */
  @Transactional
  public MemberDto.Response updateMember(MemberDto.Request memberRequest) {

    // 회원 정보를 조회
    MemberEntity member = memberRepository.findById(memberRequest.getId())
        .orElseThrow(() -> new ApiException(ApiStatus.MEMBER_NOT_FOUND));

    // 회원명과 이메일을 요청받은 정보로 수정
    Optional.ofNullable(memberRequest.getName())
        .filter(StringUtils::hasText)
        .ifPresent(member::setName);

    Optional.ofNullable(memberRequest.getEmail())
        .filter(StringUtils::hasText)
        .ifPresent(member::setEmail);

    // 수정된 회원 정보를 저장
    memberRepository.save(member);

    MemberDto.Response memberResponse = modelMapper.map(member, MemberDto.Response.class);

    // Redis에 사용할 키 생성
    String redisKey = String.format("SAMPLE:MEMBER:%s", memberRequest.getId());

    // Redis에 회원 정보를 수정
    redisComponent.setObjectValue(redisKey, memberResponse, 10L, TimeUnit.MINUTES);

    return memberResponse;
  }
}
