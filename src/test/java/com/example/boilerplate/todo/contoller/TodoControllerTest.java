package com.example.boilerplate.todo.contoller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.boilerplate.common.type.ApiStatus;
import com.example.boilerplate.sign.dto.SignDto;
import com.example.boilerplate.sign.service.SignService;
import com.example.boilerplate.todo.dto.TodoDto;
import com.example.boilerplate.todo.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@AutoConfigureMockMvc
@ActiveProfiles("local")
@SpringBootTest
class TodoControllerTest {

  private static final String PATH_STATUS_CODE = "$.statusCode";
  private static final String PATH_MESSAGE = "$.message";

  private static final String TODO_BASE_URL = "/todo";
  private static final String TODOS_BASE_URL = "/todos";

  private static final String TODO_TITLE = "할 일 제목";
  private static final String TODO_DESCRIPTION = "할 일 내용";

  private static final String CONTAINS_TODO_TITLE = "제목";
  private static final String CONTAINS_TODO_DESCRIPTION = "내용";

  private static final String UPDATED_TODO_TITLE = TODO_TITLE + " 수정";
  private static final String UPDATED_TODO_DESCRIPTION = TODO_DESCRIPTION + " 수정";

  private static final long MEMBER_ID = 3L;
  private static final String MEMBER_PASSWORD = "password1!";
  private static final String MEMBER_EMAIL = "tester@example.com";

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_TOKEN_PREFIX = "Bearer ";

  @Autowired
  MockMvc mockMvc;

  @Autowired
  TodoService todoService;

  @Autowired
  SignService signService;

  @Autowired
  EntityManager entityManager;

  @Autowired
  ObjectMapper objectMapper;

  private String token;

  @BeforeEach
  void setUp() {
    this.token = getToken();
  }

  private String getToken() {
    SignDto.Response signResponse = signService.signIn(
        SignDto.Request.of(MEMBER_EMAIL, MEMBER_PASSWORD));
    return signResponse.getToken();
  }

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

      // When
      ResultActions resultActions = mockMvc.perform(
          get(TODOS_BASE_URL)
              .header(AUTHORIZATION_HEADER, BEARER_TOKEN_PREFIX + token)
              .param("title", CONTAINS_TODO_TITLE)
              .param("description", CONTAINS_TODO_DESCRIPTION)
              .param("completed", "true")
      );

      // Then
      resultActions
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath(PATH_STATUS_CODE).value(ApiStatus.OK.getCode()))
          .andExpect(jsonPath(PATH_MESSAGE).value(ApiStatus.OK.getMessage()))
          .andExpect(jsonPath("$.data").isNotEmpty())
          .andDo(print());
    }
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("getPagedTodos - 페이징된 할 일 목록 조회")
  @Nested
  class TestGetTodoLis {

    @Order(1)
    @DisplayName("getPagedTodos - 페이징된 할 일 목록 조회 성공")
    @Transactional
    @Test
    void testGetPagedTodosSuccess() throws Exception {

      // Given
      setUpTodoList();
      clearPersistenceContext();

      // When
      ResultActions resultActions = mockMvc.perform(
          get(TODOS_BASE_URL + "/paged")
              .header(AUTHORIZATION_HEADER, BEARER_TOKEN_PREFIX + token)
              .param("title", CONTAINS_TODO_TITLE)
              .param("description", CONTAINS_TODO_DESCRIPTION)
              .param("completed", "true")
              .param("page", "0")
              .param("size", "3")
              .param("sorts", "title,desc")
      );

      // Then
      resultActions
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath(PATH_STATUS_CODE).value(ApiStatus.OK.getCode()))
          .andExpect(jsonPath(PATH_MESSAGE).value(ApiStatus.OK.getMessage()))
          .andExpect(jsonPath("$.data").isNotEmpty())
          .andDo(print());
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
      ResultActions resultActions = mockMvc.perform(
          get(TODO_BASE_URL + "/" + todoId)
              .header(AUTHORIZATION_HEADER, BEARER_TOKEN_PREFIX + token)
      );

      // Then
      resultActions
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath(PATH_STATUS_CODE).value(ApiStatus.OK.getCode()))
          .andExpect(jsonPath(PATH_MESSAGE).value(ApiStatus.OK.getMessage()))
          .andExpect(jsonPath("$.data.id").value(todoId))
          .andExpect(jsonPath("$.data.title").value(TODO_TITLE))
          .andExpect(jsonPath("$.data.description").value(TODO_DESCRIPTION))
          .andExpect(jsonPath("$.data.member.id").value(MEMBER_ID))
          .andDo(print());
    }
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("insertTodo - 할 일 추가")
  @Nested
  class TestInsertTodo {

    @Order(1)
    @DisplayName("insertTodo - 할 일 추가 성공 - POST /todo")
    @Transactional
    @Test
    void testInsertTodoSuccess() throws Exception {

      // Given
      TodoDto.InsertRequest insertTodoRequest =
          TodoDto.InsertRequest.of(MEMBER_ID, TODO_TITLE, TODO_DESCRIPTION);

      // When
      ResultActions resultActions = mockMvc.perform(
          post(TODO_BASE_URL)
              .header(AUTHORIZATION_HEADER, BEARER_TOKEN_PREFIX + token)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(insertTodoRequest)));

      // Then
      resultActions
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath(PATH_STATUS_CODE).value(ApiStatus.OK.getCode()))
          .andExpect(jsonPath(PATH_MESSAGE).value(ApiStatus.OK.getMessage()))
          .andExpect(jsonPath("$.data.member.id").value(insertTodoRequest.getMemberId()))
          .andExpect(jsonPath("$.data.title").value(insertTodoRequest.getTitle()))
          .andExpect(jsonPath("$.data.description").value(insertTodoRequest.getDescription()))
          .andExpect(jsonPath("$.data.completed").value(false))
          .andDo(print());
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
      Long todoId = saveTodoAndReturnId(TODO_TITLE, TODO_DESCRIPTION);
      TodoDto.UpdateRequest updateRequest = TodoDto.UpdateRequest.of(
          todoId, UPDATED_TODO_TITLE, UPDATED_TODO_DESCRIPTION, true);
      clearPersistenceContext();

      // When
      ResultActions resultActions = mockMvc.perform(
          put(TODO_BASE_URL)
              .header(AUTHORIZATION_HEADER, BEARER_TOKEN_PREFIX + token)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(updateRequest)));

      // Then
      resultActions
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath(PATH_STATUS_CODE).value(ApiStatus.OK.getCode()))
          .andExpect(jsonPath(PATH_MESSAGE).value(ApiStatus.OK.getMessage()))
          .andExpect(jsonPath("$.data.id").value(todoId))
          .andExpect(jsonPath("$.data.title").value(UPDATED_TODO_TITLE))
          .andExpect(jsonPath("$.data.description").value(UPDATED_TODO_DESCRIPTION))
          .andExpect(jsonPath("$.data.completed").value(true))
          .andDo(print());
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
    void testDeleteTodoSuccess() throws Exception {

      // Given
      Long todoId = saveTodoAndReturnId(TODO_TITLE, TODO_DESCRIPTION);
      clearPersistenceContext();

      // When
      ResultActions resultActions = mockMvc.perform(
          delete(TODO_BASE_URL + "/" + todoId)
              .header(AUTHORIZATION_HEADER, BEARER_TOKEN_PREFIX + token));

      // Then
      resultActions
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath(PATH_STATUS_CODE).value(ApiStatus.OK.getCode()))
          .andExpect(jsonPath(PATH_MESSAGE).value(ApiStatus.OK.getMessage()))
          .andDo(print());

      // 삭제된 할 일 재조회
      // When
      ResultActions findResultAction = mockMvc.perform(
          get(TODO_BASE_URL + "/" + todoId)
              .header(AUTHORIZATION_HEADER, BEARER_TOKEN_PREFIX + token));

      // Then
      findResultAction
          .andExpect(status().isNotFound())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath(PATH_STATUS_CODE).value(ApiStatus.TODO_NOT_FOUND.getCode()))
          .andExpect(jsonPath(PATH_MESSAGE).value(ApiStatus.TODO_NOT_FOUND.getMessage()))
          .andExpect(jsonPath("$.method").value(HttpMethod.DELETE.toString()))
          .andExpect(jsonPath("$.timestamp").isNotEmpty())
          .andDo(print());
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
      todoService.insertTodo(TodoDto.InsertRequest.of(MEMBER_ID, title, description, completed));
    }
  }

  @Disabled
  private Long saveTodoAndReturnId(String title, String description) {
    return todoService.insertTodo(
        TodoDto.InsertRequest.of(MEMBER_ID, title, description)).getId();
  }
}
