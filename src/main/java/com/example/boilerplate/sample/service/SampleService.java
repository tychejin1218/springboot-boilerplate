package com.example.boilerplate.sample.service;

import com.example.boilerplate.common.exception.ApiException;
import com.example.boilerplate.common.type.ApiStatus;
import com.example.boilerplate.sample.domain.entity.Member;
import com.example.boilerplate.sample.domain.entity.Todo;
import com.example.boilerplate.sample.domain.entity.TodoDynamic;
import com.example.boilerplate.sample.domain.mapstruct.MemberMapStruct;
import com.example.boilerplate.sample.domain.mapstruct.TodoMapStruct;
import com.example.boilerplate.sample.domain.repository.MemberRepository;
import com.example.boilerplate.sample.domain.repository.TodoDynamicRepository;
import com.example.boilerplate.sample.domain.repository.TodoRepository;
import com.example.boilerplate.sample.dto.MemberDto;
import com.example.boilerplate.sample.dto.TodoDto;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class SampleService {

  private final MemberRepository memberRepository;
  private final MemberMapStruct memberMapStruct;
  private final TodoRepository todoRepository;
  private final TodoDynamicRepository todoDynamicRepository;
  private final TodoMapStruct todoMapStruct;

  /**
   * 회원 목록 조회
   *
   * @param memberRequest 검색 조건이 포함된 MemberDto.Request 객체
   * @return Page&lt;MemberDto.Response&gt; 페이징 처리된 회원 목록
   */
  @Transactional(readOnly = true)
  public Page<MemberDto.Response> getMemberList(MemberDto.Request memberRequest) {
    Page<Member> memberPage =
        memberRepository.findAllByNameContainsAndEmailContains(
            memberRequest.getName(),
            memberRequest.getEmail(),
            memberRequest.pageRequest());
    return memberPage.map(memberMapStruct::toDto);
  }

  /**
   * 회원 ID를 통해 특정 회원의 정보를 조회
   *
   * @param memberRequest ID 정보가 포함된 MemberDto.Request 객체
   * @return MemberDto.Response 조회된 회원의 상세 정보
   */
  @Transactional(readOnly = true)
  public MemberDto.Response getMember(MemberDto.Request memberRequest) {
    Member member = memberRepository.findById(memberRequest.getId())
        .orElseThrow(() -> new ApiException(ApiStatus.MEMBER_NOT_FOUND));
    return memberMapStruct.toDto(member);
  }

  /**
   * 회원 저장
   *
   * @param memberRequest 저장할 회원 정보가 포함된 MemberDto.Request 객체
   * @return MemberDto.Response 저장된 회원의 정보
   */
  @Transactional
  public MemberDto.Response insertMember(MemberDto.Request memberRequest) {
    return memberMapStruct.toDto(memberRepository.save(memberMapStruct.toEntity(memberRequest)));
  }

  /**
   * 회원 수정
   *
   * @param memberRequest 수정할 정보가 포함된 MemberDto.Request 객체
   * @return MemberDto.Response 수정된 회원의 정보
   */
  @Transactional
  public MemberDto.Response updateMember(MemberDto.Request memberRequest) {

    Member member;

    Optional<Member> opMember = memberRepository.findById(memberRequest.getId());
    if (opMember.isPresent()) {
      member = opMember.get();

      if (StringUtils.isNotBlank(memberRequest.getName())) {
        member.setName(memberRequest.getName());
      }

      if (StringUtils.isNotBlank(memberRequest.getEmail())) {
        member.setEmail(memberRequest.getEmail());
      }

      memberRepository.save(member);
    } else {
      throw new ApiException(ApiStatus.MEMBER_NOT_FOUND);
    }

    return memberMapStruct.toDto(member);
  }

  /**
   * 할 일 목록 조회
   *
   * @param todoRequest 검색 조건이 포함된 TodoDto.Request 객체
   * @return List&lt;TodoDto.Response&gt; 검색 결과에 따른 할 일 목록
   */
  @Transactional(readOnly = true)
  public List<TodoDto.Response> getTodoList(TodoDto.Request todoRequest) {
    List<Todo> todos =
        todoRepository
            .findAllByTitleContainingIgnoreCaseAndDescriptionContainingIgnoreCaseAndCompletedOrderByIdDesc(
                todoRequest.getTitle(),
                todoRequest.getDescription(),
                todoRequest.getCompleted()
            );
    return todoMapStruct.todosToTodoResponses(todos);
  }

  /**
   * 할 일 ID를 통해 특정 할 일의 정보를 조회
   *
   * @param todoRequest ID 정보가 포함된 TodoDto.Request 객체
   * @return TodoDto.Response 조회된 할 일의 정보
   */
  @Transactional(readOnly = true)
  public TodoDto.Response getTodo(TodoDto.Request todoRequest) {
    Todo todo = todoRepository.findById(todoRequest.getId())
        .orElseThrow(() -> new ApiException(ApiStatus.TODO_NOT_FOUND));
    return todoMapStruct.toDto(todo);
  }

  /**
   * 할 일 저장
   *
   * @param todoRequest 저장할 할 일 정보가 포함된 TodoDto.Request 객체
   * @return TodoDto.Response 저장된 할 일의 정보
   */
  @Transactional
  public TodoDto.Response insertTodo(TodoDto.Request todoRequest) {
    return todoMapStruct.toDto(todoRepository.save(todoMapStruct.toEntity(todoRequest)));
  }

  /**
   * 할 일 수정
   *
   * @param todoRequest 수정할 정보가 포함된 TodoDto.Request 객체
   * @return TodoDto.Response 수정된 할 일의 정보
   */
  @Transactional
  public TodoDto.Response updateTodo(TodoDto.Request todoRequest) {

    Todo todo;

    Optional<Todo> opTodo = todoRepository.findById(todoRequest.getId());
    if (opTodo.isPresent()) {
      todo = opTodo.get();

      if (StringUtils.isNotBlank(todoRequest.getTitle())) {
        todo.setTitle(todoRequest.getTitle());
      }

      if (StringUtils.isNotBlank(todoRequest.getDescription())) {
        todo.setDescription(todoRequest.getDescription());
      }

      if (todoRequest.getCompleted() != null) {
        todo.setCompleted(todoRequest.getCompleted());
      }

      todoRepository.save(todo);
    } else {
      throw new ApiException(ApiStatus.TODO_NOT_FOUND);
    }

    return todoMapStruct.toDto(todo);
  }

  /**
   * 할 일 수정 - @DynamicUpdate - completed만 수정
   *
   * @param todoRequest 수정할 정보가 포함된 TodoDto.Request 객체
   */
  @Transactional
  public void updateTodoCompleted(TodoDto.Request todoRequest) {
    TodoDynamic todoDynamic = todoDynamicRepository.findById(todoRequest.getId())
        .orElseThrow(() -> new ApiException(ApiStatus.TODO_NOT_FOUND));
    todoDynamic.updateCompleted(todoRequest.getCompleted());
  }
}
