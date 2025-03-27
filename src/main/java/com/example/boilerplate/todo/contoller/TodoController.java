package com.example.boilerplate.todo.contoller;

import com.example.boilerplate.common.component.RedisComponent;
import com.example.boilerplate.common.constants.Constants;
import com.example.boilerplate.common.response.BaseResponse;
import com.example.boilerplate.todo.dto.TodoDto;
import com.example.boilerplate.todo.service.TodoService;
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
public class TodoController implements TodoControllerDocs {

  private final TodoService todoService;
  private final RedisComponent redisComponent;
  private static final String TODO_LIST_CACHE_KEY = "todos:list";
  private static final String TODO_CACHE_PREFIX = "todo:";

  /**
   * 할 일 목록 조회
   *
   * @return 할 일 목록
   */
  @GetMapping("/todos")
  @Override
  public BaseResponse<List<TodoDto.Response>> getTodos(
      @RequestParam(value = "title", required = false) String title,
      @RequestParam(value = "description", required = false) String description,
      @RequestParam(value = "completed", required = false) Boolean completed) {
    return BaseResponse.ok(
        todoService.getTodoList(TodoDto.Request.of(title, description, completed)));
  }

  /**
   * 페이징 처리된 할 일 목록 조회
   *
   * @return 페이징 처리된 할 일 목록
   */
  @GetMapping("/todos/paged")
  public BaseResponse<Page<TodoDto.Response>> getPagedTodos(
      @RequestParam(value = "title", required = false) String title,
      @RequestParam(value = "description", required = false) String description,
      @RequestParam(value = "completed", required = false) Boolean completed,
      @RequestParam(value = "page", required = false, defaultValue = "0") int page,
      @RequestParam(value = "size", required = false, defaultValue = "10") int size,
      @RequestParam(value = "sorts", required = false) List<String> sorts
  ) {
    // TODO : sorts 조건 하나일때
    TodoDto.PageRequest pageRequest =
        TodoDto.PageRequest.of(title, description, completed, page, size, sorts);
    Page<TodoDto.Response> pagedTodoList = todoService.getPagedTodoList(pageRequest);
    return BaseResponse.ok(pagedTodoList);
  }

  /**
   * 특정 할 일 조회
   *
   * @param id 할 일 아이디
   * @return 할 일 상세 정보
   */
  @GetMapping("/todo/{id}")
  @Override
  public BaseResponse<TodoDto.Response> getTodo(@PathVariable Long id) {
    String cacheKey = Constants.BP_CACHE_PREFIX + TODO_CACHE_PREFIX + id;
    TodoDto.Response todoResponse = redisComponent.getCacheOrDefault(cacheKey,
        new TypeReference<>() {
        },
        () -> todoService.getTodo(id),
        10, TimeUnit.MINUTES);
    return BaseResponse.ok(todoResponse);
  }

  /**
   * 할 일 추가
   *
   * @param insertTodoRequest 추가할 할 일 정보
   * @return 추가된 할 일 정보
   */
  @PostMapping("/todo")
  @Override
  public BaseResponse<TodoDto.Response> insertTodo(
      @RequestBody TodoDto.InsertRequest insertTodoRequest) {
    redisComponent.deleteKey(Constants.BP_CACHE_PREFIX + TODO_LIST_CACHE_KEY);
    return BaseResponse.ok(todoService.insertTodo(insertTodoRequest));
  }

  /**
   * 기존 할 일 수정
   *
   * @param updateTodoRequest 수정할 할 일 정보
   * @return 수정된 할 일 정보
   */
  @PutMapping("/todo")
  @Override
  public BaseResponse<TodoDto.Response> updateTodo(
      @RequestBody TodoDto.UpdateRequest updateTodoRequest) {
    TodoDto.Response todoResponse = todoService.updateTodo(updateTodoRequest);
    redisComponent.deleteKey(Constants.BP_CACHE_PREFIX + TODO_CACHE_PREFIX + todoResponse.getId());
    redisComponent.deleteKey(Constants.BP_CACHE_PREFIX + TODO_LIST_CACHE_KEY);
    return BaseResponse.ok(todoResponse);
  }

  /**
   * 할 일 삭제
   *
   * @param id 삭제할 할 일 아이디
   * @return 삭제된 할 일 정보
   */
  @DeleteMapping("/todo/{id}")
  @Override
  public BaseResponse<TodoDto.Response> deleteTodo(@PathVariable Long id) {
    TodoDto.Response deletedTodo = todoService.deleteTodo(id);
    redisComponent.deleteKey(Constants.BP_CACHE_PREFIX + TODO_CACHE_PREFIX + id);
    redisComponent.deleteKey(Constants.BP_CACHE_PREFIX + TODO_LIST_CACHE_KEY);
    return BaseResponse.ok(deletedTodo);
  }
}
