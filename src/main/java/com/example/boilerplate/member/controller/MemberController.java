package com.example.boilerplate.member.controller;

import com.example.boilerplate.common.response.BaseResponse;
import com.example.boilerplate.member.dto.MemberDto;
import com.example.boilerplate.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MemberController implements MemberControllerApiDocs {

  private final MemberService memberService;

  /**
   * 회원 목록 조회
   *
   * @param memberRequest 검색 조건이 포함된 MemberDto.Request 객체
   * @return 페이징 처리된 회원 목록
   */
  @GetMapping("/sample/members")
  @Override
  public BaseResponse<Page<MemberDto.Response>> getMemberList(
      @ModelAttribute MemberDto.Request memberRequest) {
    return BaseResponse.ok(memberService.getMemberList(memberRequest));
  }

  /**
   * 회원 조회
   *
   * @param id 회원 ID
   * @return 조회된 회원의 정보
   */
  @GetMapping("/sample/member/{id}")
  @Override
  public BaseResponse<MemberDto.Response> getMember(@PathVariable Long id) {
    return BaseResponse.ok(memberService.getMember(MemberDto.Request.of(id)));
  }

  /**
   * 회원 저장
   *
   * @param memberRequest 저장할 회원 정보가 포함된 MemberDto.Request 객체
   * @return 저장된 회원의 정보
   */
  @PostMapping("/sample/insert/member")
  @Override
  public BaseResponse<MemberDto.Response> insertMember(
      @RequestBody MemberDto.Request memberRequest) {
    return BaseResponse.ok(memberService.insertMember(memberRequest));
  }

  /**
   * 회원 수정
   *
   * @param memberRequest 수정할 정보가 포함된 MemberDto.Request 객체
   * @return 수정된 회원의 정보
   */
  @PutMapping("/sample/update/member")
  @Override
  public BaseResponse<MemberDto.Response> updateMember(
      @RequestBody MemberDto.Request memberRequest) {
    return BaseResponse.ok(memberService.updateMember(memberRequest));
  }
}
