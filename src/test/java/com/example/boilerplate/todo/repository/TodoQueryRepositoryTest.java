package com.example.boilerplate.todo.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.boilerplate.domain.entity.TodoEntity;
import com.example.boilerplate.todo.dto.TodoDto;
import com.example.boilerplate.todo.dto.TodoDto.Response;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
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

  private static final String TODO_TITLE = "할 일 제목";
  private static final String TODO_DESCRIPTION = "할 일 내용";

  @Autowired
  private TodoQueryRepository todoQueryRepository;

  @Autowired
  private EntityManager entityManager;

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("getTodoList - 할 일 목록 조회")
  @Nested
  class TestGetTodoList {

    @Order(1)
    @DisplayName("조건 없이 할 일 목록 조회")
    @Transactional
    @Test
    void testGetTodoList() {

      // Given
      setUpTodoList();
      clearPersistenceContext();

      // When
      TodoDto.Request request = TodoDto.Request.builder().build();
      List<TodoDto.Response> todoList = todoQueryRepository.getTodoList(request);

      // Then
      assertFalse(todoList.isEmpty());
    }

    @Order(2)
    @DisplayName("제목 조건으로 조회")
    @Transactional
    @Test
    void testGetTodoListByTitle() {

      // Given
      setUpTodoList();
      clearPersistenceContext();

      // When
      TodoDto.Request request = TodoDto.Request.of(TODO_TITLE + " 3", null, null);
      List<TodoDto.Response> todoList = todoQueryRepository.getTodoList(request);

      // Then
      assertEquals(TODO_TITLE + " 3", todoList.get(0).getTitle());
    }

    @Order(3)
    @DisplayName("설명 조건으로 조회")
    @Transactional
    @Test
    void testGetTodoListByDescription() {

      // Given
      setUpTodoList();
      clearPersistenceContext();

      // When
      TodoDto.Request request = TodoDto.Request.of(null, TODO_DESCRIPTION + " 2", null);
      List<TodoDto.Response> todoList = todoQueryRepository.getTodoList(request);

      // Then
      assertEquals(TODO_DESCRIPTION + " 2", todoList.get(0).getDescription());
    }

    @Order(4)
    @DisplayName("완료 여부 조건으로 조회")
    @Transactional
    @Test
    void testGetTodoListByCompleted() {

      // Given
      setUpTodoList();
      clearPersistenceContext();

      // When
      TodoDto.Request request = TodoDto.Request.of(null, null, true);
      List<TodoDto.Response> todoList = todoQueryRepository.getTodoList(request);

      // Then
      assertFalse(todoList.isEmpty());
    }

    @Order(5)
    @DisplayName("제목, 설명, 완료 여부로 조회")
    @Transactional
    @Test
    void testGetTodoListByTitleAndDescriptionAndCompleted() {

      // Given
      setUpTodoList();
      clearPersistenceContext();

      // When
      TodoDto.Request request = TodoDto.Request.of(TODO_TITLE + " 4", TODO_DESCRIPTION + " 4",
          true);
      List<TodoDto.Response> todoList = todoQueryRepository.getTodoList(request);

      // Then
      assertAll(
          () -> assertEquals(TODO_TITLE + " 4", todoList.get(0).getTitle()),
          () -> assertEquals(TODO_DESCRIPTION + " 4", todoList.get(0).getDescription()),
          () -> assertTrue(todoList.get(0).getCompleted())
      );
    }
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("getPagedTodoList - 페이징 및 조건 검색 테스트")
  @Nested
  class TestGetPagedTodoList {

    @Order(1)
    @DisplayName("페이징 조건이 포함된 할 일 목록 조회")
    @Transactional
    @Test
    void testGetPagedTodoList() {

      // Given
      setUpTodoList();
      clearPersistenceContext();
      TodoDto.PageRequest pageRequest = TodoDto.PageRequest.builder()
          .page(1)
          .size(3)
          .build();

      // When
      Page<Response> pageResult = todoQueryRepository.getPagedTodoList(pageRequest);

      // Then
      assertAll(
          () -> assertEquals(3, pageResult.getContent().size()),
          () -> assertTrue(pageResult.getTotalElements() > 0),
          () -> assertTrue(pageResult.getTotalPages() > 0)
      );
      log.info("Page Content: {}", pageResult.getContent());
    }

    @Order(2)
    @DisplayName("페이징 및 정렬 조건이 포함된 할 일 목록 조회")
    @Transactional
    @Test
    void testGetPagedTodoListWithSorting() {

      // Given
      setUpTodoList();
      clearPersistenceContext();
      TodoDto.PageRequest pageRequest = TodoDto.PageRequest.builder()
          .page(1)
          .size(3)
          .sorts(List.of("title,desc", "id,asc"))
          .build();

      // When
      Page<Response> pageResult = todoQueryRepository.getPagedTodoList(pageRequest);

      // Then
      assertAll(
          () -> assertEquals(3, pageResult.getContent().size()),
          () -> assertTrue(pageResult.getTotalElements() > 0),
          () -> assertTrue(pageResult.getTotalPages() > 0),
          () -> assertTrue(pageResult.getContent().get(0).getTitle()
              .compareTo(pageResult.getContent().get(1).getTitle()) >= 0)
      );
      log.info("Page Content: {}", pageResult.getContent());
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
      String title = TODO_TITLE + " " + i;
      String description = TODO_DESCRIPTION + " " + i;
      boolean completed = i % 2 == 0; // i가 짝수일 경우 완료 상태로 설정.
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
