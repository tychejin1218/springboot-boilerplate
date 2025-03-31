package com.example.boilerplate.todo.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.boilerplate.common.exception.ApiException;
import com.example.boilerplate.common.type.ApiStatus;
import com.example.boilerplate.domain.entity.TodoEntity;
import com.example.boilerplate.todo.dto.TodoDto;
import com.example.boilerplate.todo.dto.TodoDto.Response;
import jakarta.persistence.EntityManager;
import java.util.List;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("local")
@SpringBootTest
class TodoQueryRepositoryTest {

  private static final String TODO_TITLE_PREFIX = "할 일 제목";
  private static final String TODO_DESCRIPTION_PREFIX = "할 일 내용";

  @Autowired
  private TodoQueryRepository todoQueryRepository;

  @Autowired
  private EntityManager entityManager;

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("selectTodoList - 할 일 목록 조회")
  @Nested
  class TestSelectTodoList {

    @BeforeEach
    void setUp() {
      setUpTodoList();
      clearPersistenceContext();
    }

    @Order(1)
    @DisplayName("검색 조건 없이 할 일 목록 조회")
    @Transactional
    @Test
    void testSelectTodoList() {

      // Given
      TodoDto.Request request = TodoDto.Request.builder().build();

      // When
      List<TodoDto.Response> todoList = todoQueryRepository.selectTodoList(request);

      // Then
      assertFalse(todoList.isEmpty());
    }

    @Order(2)
    @DisplayName("제목 조건으로 조회")
    @Transactional
    @Test
    void testSelectTodoListByTitle() {

      // Given
      String todoTitle = TODO_TITLE_PREFIX + "_1";
      TodoDto.Request todoRequest = TodoDto.Request.of(todoTitle, null, null);

      // When
      List<TodoDto.Response> todoList = todoQueryRepository.selectTodoList(todoRequest);

      // Then
      assertFalse(todoList.isEmpty());
    }

    @Order(3)
    @DisplayName("내용 조건으로 조회")
    @Transactional
    @Test
    void testSelectTodoListByDescription() {

      // Given
      String todoDescription = TODO_DESCRIPTION_PREFIX + "_1";
      TodoDto.Request todoRequest = TodoDto.Request.of(null, todoDescription, null);

      // When
      List<TodoDto.Response> todoList = todoQueryRepository.selectTodoList(todoRequest);

      // Then
      assertFalse(todoList.isEmpty());
    }

    @Order(4)
    @DisplayName("완료 여부 조건으로 조회")
    @Transactional
    @Test
    void testSelectTodoListByCompleted() {

      // Given
      TodoDto.Request todoRequest = TodoDto.Request.of(null, null, true);

      // When
      List<TodoDto.Response> todoList = todoQueryRepository.selectTodoList(todoRequest);

      // Then
      assertFalse(todoList.isEmpty());
    }

    @Order(5)
    @DisplayName("제목, 설명, 완료 여부로 조회")
    @Transactional
    @Test
    void testSelectTodoListByTitleAndDescriptionAndCompleted() {

      // Given
      String todoTitle = TODO_TITLE_PREFIX + "_1";
      String todoDescription = TODO_DESCRIPTION_PREFIX + "_1";
      TodoDto.Request todoRequest = TodoDto.Request.of(todoTitle, todoDescription, false);

      // When
      List<TodoDto.Response> todoList = todoQueryRepository.selectTodoList(todoRequest);

      // Then
      assertFalse(todoList.isEmpty());
    }
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("selectPagedTodoList - 페이징이 적용된 할 일 목록 조회")
  @Nested
  class TestSelectPagedTodoList {

    @BeforeEach
    void setUp() {
      setUpTodoList();
      clearPersistenceContext();
    }

    @Order(1)
    @DisplayName("페이징이 적용된 할 일 목록 조회")
    @Transactional
    @Test
    void testSelectPagedTodoList() {

      // Given
      TodoDto.PageRequest pageRequest = TodoDto.PageRequest.of(null, null, null, 1, 3, null);

      // When
      Page<Response> pageResult = todoQueryRepository.selectPagedTodoList(pageRequest);

      // Then
      assertAll(
          () -> assertEquals(3, pageResult.getContent().size()),
          () -> assertTrue(pageResult.getTotalElements() > 0),
          () -> assertTrue(pageResult.getTotalPages() > 0)
      );
    }

    @Order(2)
    @DisplayName("페이징 및 정렬 조건이 적용된 할 일 목록 조회")
    @Transactional
    @Test
    void testSelectPagedTodoListWithSorting() {

      // Given
      TodoDto.PageRequest pageRequest = TodoDto.PageRequest.of(null, null, null, 1, 3,
          List.of("title,desc", "id,asc"));

      // When
      Page<TodoDto.Response> pageResult = todoQueryRepository.selectPagedTodoList(pageRequest);

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
    void testSelectPagedTodoListWithInvalidSortField() {

      // Given
      TodoDto.PageRequest pageRequest = TodoDto.PageRequest.of(null, null, null, 1, 3,
          List.of("invalidField,desc"));

      // When
      ApiException apiException = Assertions.assertThrows(
          ApiException.class, () -> todoQueryRepository.selectPagedTodoList(pageRequest));

      // Then
      assertAll(
          () -> assertEquals(ApiStatus.METHOD_ARGUMENT_NOT_VALID.getCode(),
              apiException.getStatus().getCode()),
          () -> assertEquals(ApiStatus.METHOD_ARGUMENT_NOT_VALID.getMessage(),
              apiException.getStatus().getMessage())
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
    for (int i = 1; i < 10; i++) {
      String title = TODO_TITLE_PREFIX + "_" + i;
      String description = TODO_DESCRIPTION_PREFIX + "_" + i;
      boolean completed = i % 2 == 0;
      entityManager.persist(
          TodoEntity.builder()
              .title(title)
              .description(description)
              .completed(completed)
              .build()
      );
    }
  }
}
