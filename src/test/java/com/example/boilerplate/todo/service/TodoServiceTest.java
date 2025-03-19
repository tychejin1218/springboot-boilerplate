package com.example.boilerplate.todo.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.boilerplate.common.exception.ApiException;
import com.example.boilerplate.common.type.ApiStatus;
import com.example.boilerplate.domain.entity.TodoEntity;
import com.example.boilerplate.domain.repository.TodoRepository;
import com.example.boilerplate.todo.dto.TodoDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
@ActiveProfiles("local")
@SpringBootTest
class TodoServiceTest {

  private static final String TODO_TITLE = "할 일 제목";
  private static final String TODO_DESCRIPTION = "할 일 내용";

  @Autowired
  TodoService todoService;

  @Autowired
  TodoRepository todoRepository;

  @Autowired
  EntityManager entityManager;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  ModelMapper modelMapper;

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("getTodoList - 할 일 목록 조회")
  @Nested
  class TestGetTodoList {

    @Order(1)
    @DisplayName("getTodoList - 할 일 목록 조회 성공")
    @Transactional
    @Test
    void testGetTodoListSuccess() throws Exception {

      // Given
      setUpTodoList();
      clearPersistenceContext();
      TodoDto.Request todoRequest = TodoDto.Request.of(TODO_TITLE, TODO_DESCRIPTION, true);

      // When
      List<TodoDto.Response> todoList = todoService.getTodoList(todoRequest);

      // Then
      log.debug("todoList: {}", objectMapper.writeValueAsString(todoList));
      assertFalse(todoList.isEmpty());
    }
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("getTodo - 특정 할 일 조회")
  @Nested
  class TestGetTodo {

    @Order(1)
    @DisplayName("getTodo - 특정 할 일 조회 성공")
    @Transactional
    @Test
    void testGetTodoSuccess() throws Exception {

      // Given
      Long todoId = saveTodoAndReturnId(TODO_TITLE, TODO_DESCRIPTION);
      clearPersistenceContext();

      // When
      TodoDto.Response todoResponse = todoService.getTodo(todoId);

      // Then
      log.debug("todoResponse: {}", objectMapper.writeValueAsString(todoResponse));
      assertAll(
          () -> assertEquals(TODO_TITLE, todoResponse.getTitle()),
          () -> assertEquals(TODO_DESCRIPTION, todoResponse.getDescription())
      );
    }

    @Order(2)
    @DisplayName("getTodo - 특정 할 일 조회 - 존재하지 않는 ID 조회")
    @Transactional
    @Test
    void testGetTodoNotFoundId() {

      // Given
      Long notFoundId = 0L;

      // When
      ApiException apiException = Assertions.assertThrows(
          ApiException.class, () -> todoService.getTodo(notFoundId));

      // Then
      assertAll(
          () -> assertEquals(ApiStatus.TODO_NOT_FOUND.getCode(),
              apiException.getStatus().getCode()),
          () -> assertEquals(ApiStatus.TODO_NOT_FOUND.getCode(), apiException.getStatus().getCode())
      );
    }
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("insertTodo_할 일 추가")
  @Nested
  class TestInsertTodo {

    @Order(1)
    @DisplayName("insertTodo - 할 일 추가 성공")
    @Transactional
    @Test
    void testInsertTodoSuccess() {

      // Given
      TodoDto.InsertRequest insertTodoRequest = TodoDto.InsertRequest.of(
          1L, TODO_TITLE, TODO_DESCRIPTION);

      // When
      TodoDto.Response todoResponse = todoService.insertTodo(insertTodoRequest);
      clearPersistenceContext();

      // Then
      TodoDto.Response savedTodoResponse = todoService.getTodo(todoResponse.getId());
      assertAll(
          () -> assertEquals(insertTodoRequest.getTitle(), savedTodoResponse.getTitle()),
          () -> assertEquals(insertTodoRequest.getDescription(), savedTodoResponse.getDescription())
      );
    }
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("updateTodo - 할 일 수정")
  @Nested
  class TestUpdateTodo {

    @Order(1)
    @DisplayName("updateTodo - 할 일 수정 성공")
    @Transactional
    @Test
    void testUpdateTodoSuccess() throws Exception {

      // Given
      TodoDto.Response existingTodoResponse = saveTodoAndReturnResponse(TODO_TITLE,
          TODO_DESCRIPTION);
      clearPersistenceContext();

      TodoDto.UpdateRequest updateTodoRequest = TodoDto.UpdateRequest.of(
          existingTodoResponse.getId(), "수정된 할 일 제목", "수정된 할 일 내용", true);

      // When
      TodoDto.Response updatedTodoResponse = todoService.updateTodo(updateTodoRequest);
      clearPersistenceContext();

      // Then
      TodoEntity savedTodo = todoRepository.findById(updatedTodoResponse.getId())
          .orElseThrow(() -> new Exception("Todo 수정 실패"));
      assertAll(
          () -> assertEquals(updateTodoRequest.getTitle(), savedTodo.getTitle()),
          () -> assertEquals(updateTodoRequest.getDescription(), savedTodo.getDescription()),
          () -> assertTrue(savedTodo.getCompleted())
      );
    }

    @Order(2)
    @DisplayName("updateTodo - 할 일 수정 실패 - 존재하지 않는 ID 조회")
    @Transactional
    @Test
    void testUpdateTodoNotFoundId() {

      // Given
      TodoDto.UpdateRequest updateTodoRequest = TodoDto.UpdateRequest.builder()
          .id(0L)
          .title("수정된 할 일")
          .description("수정된 할 일 설명")
          .completed(true)
          .build();

      // When
      ApiException apiException = Assertions.assertThrows(
          ApiException.class, () -> todoService.updateTodo(updateTodoRequest));

      // Then
      assertAll(
          () -> assertEquals(ApiStatus.TODO_NOT_FOUND.getCode(),
              apiException.getStatus().getCode()),
          () -> assertEquals(ApiStatus.TODO_NOT_FOUND.getMessage(),
              apiException.getStatus().getMessage())
      );
    }
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("deleteTodo - 할 일 삭제")
  @Nested
  class TestDeleteTodo {

    @Order(1)
    @DisplayName("deleteTodo - 할 일 삭제 성공")
    @Transactional
    @Test
    void testDeleteTodoSuccess() {

      // Given
      Long todoId = saveTodoAndReturnId(TODO_TITLE, TODO_DESCRIPTION);
      clearPersistenceContext();

      // When
      todoService.deleteTodo(todoId);

      // Then
      assertTrue(todoRepository.findById(todoId).isEmpty());
    }

    @Order(2)
    @DisplayName("deleteTodo - 할 일 삭제 실패 - 존재하지 않는 ID 조회")
    @Transactional
    @Test
    void testDeleteTodoNotFoundId() {

      // Given
      Long notFoundId = 0L;

      // When
      ApiException apiException = Assertions.assertThrows(
          ApiException.class, () -> todoService.deleteTodo(notFoundId));

      // Then
      assertAll(
          () -> assertEquals(ApiStatus.TODO_NOT_FOUND.getCode(),
              apiException.getStatus().getCode()),
          () -> assertEquals(ApiStatus.TODO_NOT_FOUND.getCode(), apiException.getStatus().getCode())
      );
    }
  }

  @Disabled
  private void clearPersistenceContext() {
    entityManager.flush();
    entityManager.clear();
  }

  @Disabled
  private void setUpTodoList() {
    for (int i = 1; i <= 5; i++) {
      String title = TODO_TITLE + " " + i;
      String description = TODO_DESCRIPTION + " " + i;
      boolean completed = i % 2 == 0;
      todoRepository.save(
          TodoEntity.builder()
              .title(title)
              .description(description)
              .completed(completed)
              .build());
    }
  }

  @Disabled
  private Long saveTodoAndReturnId(String title, String description) {
    return todoRepository.save(
        TodoEntity.builder()
            .title(title)
            .description(description)
            .completed(false)
            .build()).getId();
  }

  @Disabled
  private TodoDto.Response saveTodoAndReturnResponse(String title, String description) {
    TodoEntity todoEntity = todoRepository.save(
        TodoEntity.builder()
            .title(title)
            .description(description)
            .completed(false)
            .build());
    return modelMapper.map(todoEntity, TodoDto.Response.class);
  }
}
