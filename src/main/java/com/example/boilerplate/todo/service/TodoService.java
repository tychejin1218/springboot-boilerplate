package com.example.boilerplate.todo.service;

import com.example.boilerplate.common.exception.ApiException;
import com.example.boilerplate.common.type.ApiStatus;
import com.example.boilerplate.domain.entity.TodoDynamicEntity;
import com.example.boilerplate.domain.entity.TodoEntity;
import com.example.boilerplate.domain.repository.TodoDynamicRepository;
import com.example.boilerplate.domain.repository.TodoRepository;
import com.example.boilerplate.todo.dto.TodoDto;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
@Service
public class TodoService {

  private final TodoRepository todoRepository;
  private final TodoDynamicRepository todoDynamicRepository;
  private final ModelMapper modelMapper;

  /**
   * 할 일 목록 조회
   *
   * @param todoRequest 검색 조건이 포함된 TodoDto.Request 객체
   * @return List&lt;TodoDto.Response&gt; 검색 결과에 따른 할 일 목록
   */
  @Transactional(readOnly = true)
  public List<TodoDto.Response> getTodoList(TodoDto.Request todoRequest) {
    List<TodoEntity> todoList =
        todoRepository
            .findAllByTitleContainingIgnoreCaseAndDescriptionContainingIgnoreCaseAndCompletedOrderByIdDesc(
                todoRequest.getTitle(),
                todoRequest.getDescription(),
                todoRequest.getCompleted()
            );
    return todoList.stream()
        .map(todo -> modelMapper.map(todo, TodoDto.Response.class))
        .toList();
  }

  /**
   * 할 일 조회
   *
   * @param id 설명
   * @return 설명
   */
  @Transactional(readOnly = true)
  public TodoDto.Response getTodo(Long id) {
    TodoEntity todo = todoRepository.findById(id)
        .orElseThrow(() -> new ApiException(ApiStatus.TODO_NOT_FOUND));
    return modelMapper.map(todo, TodoDto.Response.class);
  }

  /**
   * 할 일 저장
   *
   * @param todoRequest 저장할 할 일 정보가 포함된 TodoDto.Request 객체
   * @return TodoDto.Response 저장된 할 일의 정보
   */
  @Transactional
  public TodoDto.Response insertTodo(TodoDto.Request todoRequest) {
    TodoEntity todoEntity = modelMapper.map(todoRequest, TodoEntity.class);
    TodoEntity savedEntity = todoRepository.save(todoEntity);
    return modelMapper.map(savedEntity, TodoDto.Response.class);
  }

  /**
   * 할 일 수정
   *
   * @param todoRequest 수정할 정보가 포함된 TodoDto.Request 객체
   * @return TodoDto.Response 수정된 할 일의 정보
   */
  @Transactional
  public TodoDto.Response updateTodo(TodoDto.Request todoRequest) {

    // 할 일의 정보를 조회
    TodoEntity todo = todoRepository.findById(todoRequest.getId())
        .orElseThrow(() -> new ApiException(ApiStatus.TODO_NOT_FOUND));

    // 제목, 상세한 설명, 완료 여부를 요청받은 정보로 수정
    Optional.ofNullable(todoRequest.getTitle())
        .filter(StringUtils::hasText)
        .ifPresent(todo::setTitle);

    Optional.ofNullable(todoRequest.getDescription())
        .filter(StringUtils::hasText)
        .ifPresent(todo::setDescription);

    Optional.ofNullable(todoRequest.getCompleted())
        .ifPresent(todo::setCompleted);

    // 수정된 할 일 정보를 저장
    todoRepository.save(todo);

    return modelMapper.map(todo, TodoDto.Response.class);
  }

  /**
   * 할 일 수정 - @DynamicUpdate - completed만 수정
   *
   * @param todoRequest 수정할 정보가 포함된 TodoDto.Request 객체
   */
  @Transactional
  public void updateTodoCompleted(TodoDto.Request todoRequest) {
    TodoDynamicEntity todoDynamic = todoDynamicRepository.findById(todoRequest.getId())
        .orElseThrow(() -> new ApiException(ApiStatus.TODO_NOT_FOUND));
    todoDynamic.updateCompleted(todoRequest.getCompleted());
  }
}
