package com.example.boilerplate.todo.contoller;

import com.example.boilerplate.common.response.BaseResponse;
import com.example.boilerplate.todo.dto.TodoDto;
import com.example.boilerplate.todo.service.TodoService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class TodoController implements TodoControllerApiDocs {

  private final TodoService todoService;

  /**
   * 할 일 목록 조회
   *
   * @param todoRequest 검색 조건이 포함된 TodoDto.Request 객체
   * @return 검색 결과에 따른 할 일 목록
   */
  @GetMapping("/sample/todos")
  @Override
  public BaseResponse<List<TodoDto.Response>> getTodos(
      @ModelAttribute TodoDto.Request todoRequest) {
    return BaseResponse.ok(todoService.getTodoList(todoRequest));
  }

  /**
   * 할 일 조회
   *
   * @param id 할 일 ID
   * @return 조회된 할 일의 정보
   */
  @GetMapping("/sample/todo/{id}")
  @Override
  public BaseResponse<TodoDto.Response> getTodo(@PathVariable Long id) {
    return BaseResponse.ok(todoService.getTodo(TodoDto.Request.of(id)));
  }

  /**
   * 할 일 저장
   *
   * @param todoRequest 저장할 할 일 정보가 포함된 TodoDto.Request 객체
   * @return 저장된 할 일의 정보
   */
  @PostMapping("/sample/insert/todo")
  @Override
  public BaseResponse<TodoDto.Response> insertTodo(
      @RequestBody TodoDto.Request todoRequest) {
    return BaseResponse.ok(todoService.insertTodo(todoRequest));
  }

  /**
   * 할 일 수정
   *
   * @param todoRequest 수정할 정보가 포함된 TodoDto.Request 객체
   * @return 수정된 할 일의 정보
   */
  @PutMapping("/sample/update/todo")
  @Override
  public BaseResponse<TodoDto.Response> updateTodo(
      @RequestBody TodoDto.Request todoRequest) {
    return BaseResponse.ok(todoService.updateTodo(todoRequest));
  }
}
