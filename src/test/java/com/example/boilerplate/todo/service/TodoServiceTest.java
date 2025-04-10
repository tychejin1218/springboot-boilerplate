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
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
@ActiveProfiles("local")
@SpringBootTest
class TodoServiceTest {

  private static final long MEMBER_ID = 3L;
  private static final String TODO_TITLE_PREFIX = "할 일 제목";
  private static final String TODO_DESCRIPTION_PREFIX = "할 일 내용";

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

    @BeforeEach
    void setUp() {
      setUpTodoList();
      clearPersistenceContext();
    }

    @Order(1)
    @DisplayName("할 일 목록 조회")
    @Transactional
    @Test
    void testGetTodoList() {

      // Given
      String todoTitle = TODO_TITLE_PREFIX + "_1";
      String todoDescription = TODO_DESCRIPTION_PREFIX + "_1";
      TodoDto.Request todoRequest = TodoDto.Request.of(todoTitle, todoDescription, true);

      // When
      List<TodoDto.Response> todoList = todoService.getTodoList(todoRequest);

      // Then
      assertFalse(todoList.isEmpty());
    }
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("getPagedTodoList - 페이징이 적용된 할 일 목록 조회")
  @Nested
  class TestGetPagedTodoList {

    @BeforeEach
    void setUp() {
      setUpTodoList();
      clearPersistenceContext();
    }

    @Order(1)
    @DisplayName("페이징이 적용된 할 일 목록 조회")
    @Transactional
    @Test
    void testGetPagedTodoListSuccess() {

      // Given
      TodoDto.PageRequest pageRequest = TodoDto.PageRequest.of(null, null, null, 1, 3);

      // When
      Page<TodoDto.Response> pageResult = todoService.getPagedTodoList(pageRequest);

      // Then
      assertAll(
          () -> assertEquals(3, pageResult.getContent().size()),
          () -> assertTrue(pageResult.getTotalElements() > 0),
          () -> assertTrue(pageResult.getTotalPages() > 0)
      );
    }

    @Order(2)
    @DisplayName("페이징 및 정렬 조건이 포함된 할 일 목록 조회")
    @Transactional
    @Test
    void testGetPagedTodoListWithSorting() {

      // Given
      TodoDto.PageRequest pageRequest = TodoDto.PageRequest.of(null, null, true, 1, 3,
          List.of("title,desc", "id,asc"));

      // When
      Page<TodoDto.Response> pageResult = todoService.getPagedTodoList(pageRequest);

      // Then
      assertAll(
          () -> assertEquals(3, pageResult.getContent().size()),
          () -> assertTrue(pageResult.getTotalElements() > 0),
          () -> assertTrue(pageResult.getTotalPages() > 0),
          () -> assertTrue(pageResult.getContent().get(0).getTitle()
              .compareTo(pageResult.getContent().get(1).getTitle()) >= 0)
      );
    }

    @Order(3)
    @DisplayName("잘못된 정렬 필드 요청 시 예외 발생")
    @Transactional
    @Test
    void testGetPagedTodoListWithInvalidSortField() {

      // Given
      TodoDto.PageRequest pageRequest = TodoDto.PageRequest.of(null, null, null, 1, 3,
          List.of("invalidField,desc"));

      // When
      ApiException apiException = Assertions.assertThrows(
          ApiException.class, () -> todoService.getPagedTodoList(pageRequest));

      // Then
      assertAll(
          () -> assertEquals(ApiStatus.METHOD_ARGUMENT_NOT_VALID.getCode(),
              apiException.getStatus().getCode()),
          () -> assertEquals(ApiStatus.METHOD_ARGUMENT_NOT_VALID.getMessage(),
              apiException.getStatus().getMessage())
      );
    }
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("getTodo - 특정 할 일 조회")
  @Nested
  class TestGetTodo {

    @Order(1)
    @DisplayName("특정 할 일 조회 성공")
    @Transactional
    @Test
    void testGetTodoSuccess() {

      // Given
      String uniqueId = UUID.randomUUID().toString().substring(0, 8);
      String todoTitle = TODO_TITLE_PREFIX + uniqueId;
      String todoDescription = TODO_DESCRIPTION_PREFIX + uniqueId;
      long todoId = saveTodoAndReturnId(todoTitle, todoDescription);
      clearPersistenceContext();

      // When
      TodoDto.Response todoResponse = todoService.getTodo(todoId);

      // Then
      assertAll(
          () -> assertEquals(todoTitle, todoResponse.getTitle()),
          () -> assertEquals(todoDescription, todoResponse.getDescription())
      );
    }

    @Order(2)
    @DisplayName("존재하지 않는 할 일 아이디로 조회 실패")
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
          () -> assertEquals(ApiStatus.TODO_NOT_FOUND.getMessage(),
              apiException.getStatus().getMessage())
      );
    }
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("insertTodo - 할 일 추가")
  @Nested
  class TestInsertTodo {

    @Order(1)
    @DisplayName("할 일 추가 성공")
    @Transactional
    @Test
    void testInsertTodoSuccess() {

      // Given
      TodoDto.InsertRequest insertTodoRequest = TodoDto.InsertRequest.of(
          MEMBER_ID, TODO_TITLE_PREFIX, TODO_DESCRIPTION_PREFIX);

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
    @DisplayName("할 일 수정 성공")
    @Transactional
    @Test
    void testUpdateTodoSuccess() throws Exception {

      // Given
      TodoDto.Response existingTodoResponse = saveTodoAndReturnResponse(TODO_TITLE_PREFIX,
          TODO_DESCRIPTION_PREFIX);
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
    @DisplayName("존재하지 않는 할 일 아이디로 수정 시 실패")
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
  @DisplayName("updateTodoCompleted - 할 일 완료 상태 변경")
  @Nested
  class TestUpdateTodoCompleted {

    @Order(1)
    @DisplayName("할 일 완료 상태 변경 성공")
    @Transactional
    @Test
    void testUpdateTodoCompletedSuccess() {

      // Given
      TodoDto.Response existingTodoResponse = saveTodoAndReturnResponse(TODO_TITLE_PREFIX,
          TODO_DESCRIPTION_PREFIX);
      clearPersistenceContext();

      TodoDto.DynamicRequest dynamicRequest = TodoDto.DynamicRequest.of(
          existingTodoResponse.getId(), true);

      // When
      todoService.updateTodoCompleted(dynamicRequest);
      clearPersistenceContext();

      // Then
      TodoEntity updatedTodo = todoRepository.findById(dynamicRequest.getId())
          .orElseThrow(() -> new RuntimeException("Todo 수정 실패"));
      assertTrue(updatedTodo.getCompleted());
    }

    @Order(2)
    @DisplayName("존재하지 않는 할 일 ID로 완료 상태 변경 시 실패")
    @Transactional
    @Test
    void testUpdateTodoCompletedNotFoundId() {

      // Given
      TodoDto.DynamicRequest dynamicRequest = TodoDto.DynamicRequest.of(0L, true);

      // When
      ApiException apiException = Assertions.assertThrows(
          ApiException.class, () -> todoService.updateTodoCompleted(dynamicRequest)
      );

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
    @DisplayName("할 일 삭제 성공")
    @Transactional
    @Test
    void testDeleteTodoSuccess() {

      // Given
      Long todoId = saveTodoAndReturnId(TODO_TITLE_PREFIX, TODO_DESCRIPTION_PREFIX);
      clearPersistenceContext();

      // When
      todoService.deleteTodo(todoId);

      // Then
      assertTrue(todoRepository.findById(todoId).isEmpty());
    }

    @Order(2)
    @DisplayName("존재하지 않는 할 일 아이디 삭제 시 실패")
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
    for (int i = 1; i <= 10; i++) {
      String title = TODO_TITLE_PREFIX + "_" + i;
      String description = TODO_DESCRIPTION_PREFIX + "_" + i;
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
