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
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

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
  public MemberDto.Response getMember(MemberDto.Request memberRequest) {
    Member member = memberRepository.findById(memberRequest.getId())
        .orElseThrow(() -> new ApiException(ApiStatus.NOT_FOUND, "존재하지 않는 Member 정보입니다."));
    return memberMapStruct.toDto(member);
  }

  /**
   * To-Do 목록 조회
   */
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
  public TodoDto.Response getTodo(Long id) {
    Todo todo = todoRepository.findById(id)
        .orElseThrow(() -> new ApiException(ApiStatus.NOT_FOUND, "존재하지 않는 To-Do 정보입니다."));
    return todoMapStruct.toDto(todo);
  }

  /**
   * Member 저장
   */
  public MemberDto.Response insertMember(MemberDto.Request memberRequest) {
    return memberMapStruct.toDto(memberRepository.save(memberMapStruct.toEntity(memberRequest)));
  }

  /**
   * To-Do 저장
   */
  public TodoDto.Response insertTodo(TodoDto.Request todoRequest) {
    return todoMapStruct.toDto(todoRepository.save(todoMapStruct.toEntity(todoRequest)));
  }

  /**
   * To-Do 수정 - @DynamicUpdate - completed만 수정
   */
  public void updateTodoCompleted(TodoDto.Request todoRequest) {
    TodoDynamic todoDynamic = todoDynamicRepository.findById(todoRequest.getId()).get();
    todoDynamic.updateCompleted(todoRequest.getCompleted());
  }
}
