package com.example.boilerplate.todo.service;

import com.example.boilerplate.common.exception.ApiException;
import com.example.boilerplate.common.type.ApiStatus;
import com.example.boilerplate.domain.entity.MemberEntity;
import com.example.boilerplate.domain.entity.TodoDynamicEntity;
import com.example.boilerplate.domain.entity.TodoEntity;
import com.example.boilerplate.domain.repository.MemberRepository;
import com.example.boilerplate.domain.repository.TodoDynamicRepository;
import com.example.boilerplate.domain.repository.TodoRepository;
import com.example.boilerplate.todo.dto.TodoDto;
import com.example.boilerplate.todo.repository.TodoQueryRepository;
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
public class TodoService {

  private final TodoRepository todoRepository;
  private final TodoQueryRepository todoQueryRepository;
  private final TodoDynamicRepository todoDynamicRepository;
  private final MemberRepository memberRepository;
  private final ModelMapper modelMapper;

  /**
   * 할 일 목록 조회
   *
   * @param todoRequest 검색 조건
   * @return 할 일 목록
   */
  @Transactional(readOnly = true)
  public List<TodoDto.Response> getTodoList(TodoDto.Request todoRequest) {
    return todoQueryRepository.selectTodoList(todoRequest);
  }

  /**
   * 페이징 적용된 할 일 목록 조회
   *
   * @param pageRequest 검색 및 페이징 조건
   * @return 할 일 목록
   */
  @Transactional(readOnly = true)
  public Page<TodoDto.Response> getPagedTodoList(TodoDto.PageRequest pageRequest) {
    return todoQueryRepository.selectPagedTodoList(pageRequest);
  }

  /**
   * 특정 할 일 조회
   *
   * @param id 할 일 아이디
   * @return 할 일 상세 정보
   */
  @Transactional(readOnly = true)
  public TodoDto.Response getTodo(Long id) {
    TodoEntity todoEntity = getTodoEntity(id);
    return modelMapper.map(todoEntity, TodoDto.Response.class);
  }

  /**
   * 할 일 추가
   *
   * @param insertTodoRequest 추가할 할 일 정보
   * @return 추가된 할 일 정보
   */
  @Transactional
  public TodoDto.Response insertTodo(TodoDto.InsertRequest insertTodoRequest) {

    MemberEntity memberEntity = memberRepository.findById(insertTodoRequest.getMemberId())
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, ApiStatus.MEMBER_NOT_FOUND));

    TodoEntity todoEntity = modelMapper.map(insertTodoRequest, TodoEntity.class);
    todoEntity.setMemberId(memberEntity.getId());

    TodoEntity savedEntity = todoRepository.save(todoEntity);

    return modelMapper.map(savedEntity, TodoDto.Response.class);
  }

  /**
   * 할 일 수정
   *
   * @param updateTodoRequest 수정할 할 일 정보
   * @return 수정된 할 일 정보
   */
  @Transactional
  public TodoDto.Response updateTodo(TodoDto.UpdateRequest updateTodoRequest) {

    TodoEntity todoEntity = getTodoEntity(updateTodoRequest.getId());

    if (StringUtils.hasText(updateTodoRequest.getTitle())) {
      todoEntity.setTitle(updateTodoRequest.getTitle());
    }
    if (StringUtils.hasText(updateTodoRequest.getDescription())) {
      todoEntity.setDescription(updateTodoRequest.getDescription());
    }
    if (updateTodoRequest.getCompleted() != null) {
      todoEntity.setCompleted(updateTodoRequest.getCompleted());
    }

    TodoEntity updatedEntity = todoRepository.save(todoEntity);
    return modelMapper.map(updatedEntity, TodoDto.Response.class);
  }

  /**
   * 할 일 삭제
   *
   * @param id 삭제할 할 일의 아이디
   * @return 삭제된 할 일 정보
   */
  @Transactional
  public TodoDto.Response deleteTodo(Long id) {
    TodoEntity todoEntity = getTodoEntity(id);
    todoRepository.delete(todoEntity);
    return modelMapper.map(todoEntity, TodoDto.Response.class);
  }

  /**
   * 할 일 완료 상태 변경
   *
   * @param dynamicRequest 변경할 할 일 정보
   */
  @Transactional
  public void updateTodoCompleted(TodoDto.DynamicRequest dynamicRequest) {
    TodoDynamicEntity todoDynamic = todoDynamicRepository.findById(dynamicRequest.getId())
        .orElseThrow(() -> new ApiException(ApiStatus.TODO_NOT_FOUND));
    todoDynamic.updateCompleted(dynamicRequest.getCompleted());
    todoDynamicRepository.save(todoDynamic);
  }

  /**
   * 할 일 엔티티 조회
   *
   * @param todoId 할 일 아이디
   * @return 할 일 엔티티
   */
  private TodoEntity getTodoEntity(Long todoId) {
    return todoRepository.findById(todoId)
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, ApiStatus.TODO_NOT_FOUND));
  }
}
