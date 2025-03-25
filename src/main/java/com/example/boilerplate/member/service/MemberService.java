package com.example.boilerplate.member.service;

import com.example.boilerplate.common.exception.ApiException;
import com.example.boilerplate.common.type.ApiStatus;
import com.example.boilerplate.domain.entity.MemberEntity;
import com.example.boilerplate.domain.repository.MemberRepository;
import com.example.boilerplate.member.dto.MemberDto;
import com.example.boilerplate.member.dto.MemberDto.Response;
import com.example.boilerplate.member.repository.MemberQueryRepository;
import java.util.List;
import java.util.Optional;
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
  public List<Response> getMemberList(MemberDto.Request memberRequest) {
    return memberQueryRepository.getMemberList(memberRequest);
  }

  /**
   * 페이징 처리된 회원 목록 조회
   *
   * @param pageRequest 검색 및 페이징 조건
   * @return 회원 목록
   */
  @Transactional(readOnly = true)
  public Page<MemberDto.Response> getMemberList(MemberDto.PageRequest pageRequest) {
    return memberQueryRepository.getPagedMemberList(pageRequest);
  }

  /**
   * 회원 ID를 통해 특정 회원의 정보를 조회
   *
   * @param id 회원 아이디
   * @return 회원 상세 정보
   */
  @Transactional(readOnly = true)
  public MemberDto.Response getMember(Long id) {
    MemberEntity memberEntity = memberRepository.findById(id)
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, ApiStatus.MEMBER_NOT_FOUND));
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

    Optional<MemberEntity> optMemberEntity = memberRepository.findByEmail(
        insertMemberRequest.getEmail());
    if (optMemberEntity.isPresent()) {
      throw new ApiException(ApiStatus.ALREADY_EXISTS_EMAIL);
    }

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

    MemberEntity memberEntity = memberRepository.findById(updateMemberRequest.getId())
        .orElseThrow(() -> new ApiException(ApiStatus.MEMBER_NOT_FOUND));

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
    memberRepository.deleteById(id);
    return modelMapper.map(memberEntity, MemberDto.Response.class);
  }
}
