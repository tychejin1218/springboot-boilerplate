package com.example.boilerplate.member.controller;

import com.example.boilerplate.common.component.RedisComponent;
import com.example.boilerplate.common.constants.Constants;
import com.example.boilerplate.common.response.BaseResponse;
import com.example.boilerplate.member.dto.MemberDto;
import com.example.boilerplate.member.service.MemberService;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MemberController {

  private final MemberService memberService;
  private final RedisComponent redisComponent;
  private static final String MEMBER_LIST_CACHE_KEY = "members:list";
  private static final String MEMBER_CACHE_PREFIX = "member:";

  /**
   * 회원 목록 조회
   *
   * @return 회원 목록
   */
  @GetMapping("/members")
  public BaseResponse<List<MemberDto.Response>> getMembers(
      @RequestParam(value = "email", required = false) String email,
      @RequestParam(value = "name", required = false) String name) {
    return BaseResponse.ok(
        memberService.getMemberList(MemberDto.Request.of(email, name)));
  }

  /**
   * 페이징 처리된 회원 목록 조회
   *
   * @return 페이징 처리된 회원 목록
   */
  @GetMapping("/members/paged")
  public BaseResponse<Page<MemberDto.Response>> getMemberList(
      @RequestParam(value = "email", required = false) String email,
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "page", required = false, defaultValue = "0") int page,
      @RequestParam(value = "size", required = false, defaultValue = "10") int size,
      @RequestParam(value = "sorts", required = false) List<String> sorts
  ) {
    // TODO : sorts 조건 하나일때
    MemberDto.PageRequest pageRequest = MemberDto.PageRequest.of(email, name, page, size, sorts);
    Page<MemberDto.Response> pagedTodoList = memberService.getPagedMemberList(pageRequest);
    return BaseResponse.ok(pagedTodoList);
  }

  /**
   * 특정 회원 조회
   *
   * @param id 회원 아이디
   * @return 회원 정보
   */
  @GetMapping("/member/{id}")
  public BaseResponse<MemberDto.Response> getTodo(@PathVariable Long id) {
    String cacheKey = Constants.BP_CACHE_PREFIX + MEMBER_CACHE_PREFIX + id;
    MemberDto.Response memberResponse = redisComponent.getCacheOrDefault(cacheKey,
        new TypeReference<>() {
        },
        () -> memberService.getMember(id),
        10, TimeUnit.MINUTES);
    return BaseResponse.ok(memberResponse);
  }

  /**
   * 회원 추가
   *
   * @param insertMemberRequest 추가할 회원 정보
   * @return 추가된 회원 정보
   */
  @PostMapping("/member")
  public BaseResponse<MemberDto.Response> insertTodo(
      @RequestBody MemberDto.InsertRequest insertMemberRequest) {
    redisComponent.deleteKey(Constants.BP_CACHE_PREFIX + MEMBER_LIST_CACHE_KEY);
    return BaseResponse.ok(memberService.insertMember(insertMemberRequest));
  }

  /**
   * 회원 수정
   *
   * @param updateMemberRequest 수정할 회원 정보
   * @return 수정된 회원 정보
   */
  @PutMapping("/member")
  public BaseResponse<MemberDto.Response> updateTodo(
      @RequestBody MemberDto.UpdateRequest updateMemberRequest) {
    MemberDto.Response memberResponse = memberService.updateMember(updateMemberRequest);
    redisComponent.deleteKey(
        Constants.BP_CACHE_PREFIX + MEMBER_CACHE_PREFIX + memberResponse.getId());
    redisComponent.deleteKey(Constants.BP_CACHE_PREFIX + MEMBER_LIST_CACHE_KEY);
    return BaseResponse.ok(memberResponse);
  }

  /**
   * 회원 삭제
   *
   * @param id 삭제할 회원 아이디
   * @return 삭제된 회원 정보
   */
  @DeleteMapping("/member/{id}")
  public BaseResponse<MemberDto.Response> deleteMember(@PathVariable Long id) {
    MemberDto.Response deletedTodo = memberService.deleteMember(id);
    redisComponent.deleteKey(Constants.BP_CACHE_PREFIX + MEMBER_CACHE_PREFIX + id);
    redisComponent.deleteKey(Constants.BP_CACHE_PREFIX + MEMBER_LIST_CACHE_KEY);
    return BaseResponse.ok(deletedTodo);
  }
}
