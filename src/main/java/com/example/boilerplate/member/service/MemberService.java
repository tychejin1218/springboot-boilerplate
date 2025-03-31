package com.example.boilerplate.member.service;

import com.example.boilerplate.common.exception.ApiException;
import com.example.boilerplate.common.type.ApiStatus;
import com.example.boilerplate.domain.entity.MemberEntity;
import com.example.boilerplate.domain.repository.MemberRepository;
import com.example.boilerplate.member.dto.MemberDto;
import com.example.boilerplate.member.repository.MemberQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

  private final MemberRepository memberRepository;
  private final MemberQueryRepository memberQueryRepository;
  private final ModelMapper modelMapper;

  /**
   * 회원 목록 조회
   *
   * @param memberRequest 검색 조건
   * @return 회원 목록
   */
  @Transactional(readOnly = true)
  public List<MemberDto.Response> getMemberList(MemberDto.Request memberRequest) {
    return memberQueryRepository.selectMemberList(memberRequest);
  }

  /**
   * 페이징 적용된 회원 목록 조회
   *
   * @param pageRequest 검색 및 페이징 조건
   * @return 회원 목록
   */
  @Transactional(readOnly = true)
  public Page<MemberDto.Response> getPagedMemberList(MemberDto.PageRequest pageRequest) {
    return memberQueryRepository.selectPagedMemberList(pageRequest);
  }

  /**
   * 특정 회원 조회
   *
   * @param id 회원 아이디
   * @return 회원 정보
   */
  @Transactional(readOnly = true)
  public MemberDto.Response getMember(Long id) {
    MemberEntity memberEntity = getMemberEntity(id);
    return modelMapper.map(memberEntity, MemberDto.Response.class);
  }

  /**
   * 회원 추가
   *
   * @param insertMemberRequest 추가할 회원 정보
   * @return 추가된 회원 정보
   */
  @Transactional
  public MemberDto.Response insertMember(MemberDto.InsertRequest insertMemberRequest) {

    memberRepository.findByEmail(insertMemberRequest.getEmail())
        .ifPresent(m -> {
          throw new ApiException(ApiStatus.ALREADY_EXISTS_EMAIL);
        });

    MemberEntity memberEntity = modelMapper.map(insertMemberRequest, MemberEntity.class);

    return modelMapper.map(memberRepository.save(memberEntity), MemberDto.Response.class);
  }

  /**
   * 회원 수정
   *
   * @param updateMemberRequest 수정할 회원 정보
   * @return 수정된 회원 정보
   */
  @Transactional
  public MemberDto.Response updateMember(MemberDto.UpdateRequest updateMemberRequest) {

    MemberEntity memberEntity = getMemberEntity(updateMemberRequest.getId());

    if (StringUtils.hasText(updateMemberRequest.getName())) {
      memberEntity.setName(updateMemberRequest.getName());
    }

    if (StringUtils.hasText(updateMemberRequest.getEmail())) {
      memberEntity.setEmail(updateMemberRequest.getEmail());
    }

    MemberEntity updatedEntity = memberRepository.save(memberEntity);
    return modelMapper.map(updatedEntity, MemberDto.Response.class);
  }

  /**
   * 회원 삭제
   *
   * @param id 삭제할 회원의 아이디
   * @return 삭제된 회원 정보
   */
  @Transactional
  public MemberDto.Response deleteMember(Long id) {
    MemberEntity memberEntity = memberRepository.findById(id)
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, ApiStatus.MEMBER_NOT_FOUND));
    memberRepository.delete(memberEntity);
    return modelMapper.map(memberEntity, MemberDto.Response.class);
  }

  /**
   * 회원 엔티티 조회
   *
   * @param memberId 회원 아이디
   * @return 회원 엔티티
   */
  private MemberEntity getMemberEntity(Long memberId) {
    return memberRepository.findById(memberId)
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, ApiStatus.MEMBER_NOT_FOUND));
  }
}
