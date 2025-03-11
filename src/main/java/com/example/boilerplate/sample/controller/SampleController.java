package com.example.boilerplate.sample.controller;

import com.example.boilerplate.common.response.BaseResponse;
import com.example.boilerplate.sample.dto.MemberDto;
import com.example.boilerplate.sample.dto.TodoDto;
import com.example.boilerplate.sample.dto.TodoDto.Response;
import com.example.boilerplate.sample.service.SampleService;
import java.util.List;
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
public class SampleController {

  private final SampleService sampleService;

  /**
   * 회원 목록 조회
   *
   * @param memberRequest 검색 조건이 포함된 MemberDto.Request 객체
   * @return 페이징 처리된 회원 목록
   */
  @GetMapping("/sample/members")
  public BaseResponse<Page<MemberDto.Response>> getMemberList(
      @ModelAttribute MemberDto.Request memberRequest) {
    return BaseResponse.ok(sampleService.getMemberList(memberRequest));
  }

  /**
   * 회원 조회
   *
   * @param id 회원 ID
   * @return 조회된 회원의 정보
   */
  @GetMapping("/sample/member/{id}")
  public BaseResponse<MemberDto.Response> getMember(@PathVariable Long id) {
    return BaseResponse.ok(sampleService.getMember(MemberDto.Request.of(id)));
  }

  /**
   * 회원 저장
   *
   * @param memberRequest 저장할 회원 정보가 포함된 MemberDto.Request 객체
   * @return 저장된 회원의 정보
   */
  @PostMapping("/sample/insert/member")
  public BaseResponse<MemberDto.Response> insertMember(
      @RequestBody MemberDto.Request memberRequest) {
    return BaseResponse.ok(sampleService.insertMember(memberRequest));
  }

  /**
   * 회원 수정
   *
   * @param memberRequest 수정할 정보가 포함된 MemberDto.Request 객체
   * @return 수정된 회원의 정보
   */
  @PutMapping("/sample/update/member")
  public BaseResponse<MemberDto.Response> updateMember(
      @RequestBody MemberDto.Request memberRequest) {
    return BaseResponse.ok(sampleService.updateMember(memberRequest));
  }

  /**
   * 할 일 목록 조회
   *
   * @param todoRequest 검색 조건이 포함된 TodoDto.Request 객체
   * @return 검색 결과에 따른 할 일 목록
   */
  @GetMapping("/sample/todos")
  public BaseResponse<List<Response>> getTodos(
      @ModelAttribute TodoDto.Request todoRequest) {
    return BaseResponse.ok(sampleService.getTodoList(todoRequest));
  }

  /**
   * 할 일 조회
   *
   * @param id 할 일 ID
   * @return 조회된 할 일의 정보
   */
  @GetMapping("/sample/todo/{id}")
  public BaseResponse<TodoDto.Response> getTodo(@PathVariable Long id) {
    return BaseResponse.ok(sampleService.getTodo(TodoDto.Request.of(id)));
  }

  /**
   * 할 일 저장
   *
   * @param todoRequest 저장할 할 일 정보가 포함된 TodoDto.Request 객체
   * @return 저장된 할 일의 정보
   */
  @PostMapping("/sample/insert/todo")
  public BaseResponse<TodoDto.Response> insertTodo(
      @RequestBody TodoDto.Request todoRequest) {
    return BaseResponse.ok(sampleService.insertTodo(todoRequest));
  }

  /**
   * 할 일 수정
   *
   * @param todoRequest 수정할 정보가 포함된 TodoDto.Request 객체
   * @return 수정된 할 일의 정보
   */
  @PutMapping("/sample/update/todo")
  public BaseResponse<TodoDto.Response> updateTodo(
      @RequestBody TodoDto.Request todoRequest) {
    return BaseResponse.ok(sampleService.updateTodo(todoRequest));
  }
}
