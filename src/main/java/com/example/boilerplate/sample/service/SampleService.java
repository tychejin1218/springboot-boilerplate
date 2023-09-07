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
   * Member 목록 조회
   */
  @Transactional(readOnly = true)
  public Page<MemberDto.Response> getMembers(MemberDto.Request memberRequest) {
    Page<Member> memberPage =
        memberRepository.findAllByNameContainsAndEmailContains(
            memberRequest.getName(),
            memberRequest.getEmail(),
            memberRequest.pageRequest());
    return memberPage.map(member -> memberMapStruct.toDto(member));
  }

  /**
   * Member 상세 조회
   */
  @Transactional(readOnly = true)
  public MemberDto.Response getMember(MemberDto.Request memberRequest) {
    Member member = memberRepository.findById(memberRequest.getId())
        .orElseThrow(() -> new ApiException(ApiStatus.NOT_FOUND, "존재하지 않는 Member 정보입니다."));
    return memberMapStruct.toDto(member);
  }

  /**
   * Member 저장
   */
  @Transactional
  public MemberDto.Response insertMember(MemberDto.Request memberRequest) {
    return memberMapStruct.toDto(memberRepository.save(memberMapStruct.toEntity(memberRequest)));
  }

  /**
   * Member 수정
   */
  @Transactional
  public MemberDto.Response updateMember(MemberDto.Request memberRequest) {

    Member member = Member.builder().build();

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
    }

    return memberMapStruct.toDto(member);
  }

  /**
   * To-Do 목록 조회
   */
  @Transactional(readOnly = true)
  public List<TodoDto.Response> getTodos(TodoDto.Request todoRequest) {

    log.info("todoRequest:[{}]", todoRequest);

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
   * To-Do 상세 조회
   */
  @Transactional(readOnly = true)
  public TodoDto.Response getTodo(TodoDto.Request todoRequest) {
    Todo todo = todoRepository.findById(todoRequest.getId())
        .orElseThrow(() -> new ApiException(ApiStatus.NOT_FOUND, "존재하지 않는 To-Do 정보입니다."));
    return todoMapStruct.toDto(todo);
  }

  /**
   * To-Do 저장
   */
  @Transactional
  public TodoDto.Response insertTodo(TodoDto.Request todoRequest) {
    return todoMapStruct.toDto(todoRepository.save(todoMapStruct.toEntity(todoRequest)));
  }

  /**
   * To-Do 수정
   */
  @Transactional
  public TodoDto.Response updateTodo(TodoDto.Request todoRequest) {

    Todo todo = Todo.builder().build();

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
    }

    return todoMapStruct.toDto(todo);
  }

  /**
   * To-Do 수정 - @DynamicUpdate - completed만 수정
   */
  @Transactional
  public void updateTodoCompleted(TodoDto.Request todoRequest) {
    TodoDynamic todoDynamic = todoDynamicRepository.findById(todoRequest.getId()).get();
    todoDynamic.updateCompleted(todoRequest.getCompleted());
  }
}
