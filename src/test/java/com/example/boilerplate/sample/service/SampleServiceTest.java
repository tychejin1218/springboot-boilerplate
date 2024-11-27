package com.example.boilerplate.sample.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.boilerplate.common.exception.ApiException;
import com.example.boilerplate.common.type.ApiStatus;
import com.example.boilerplate.sample.domain.entity.Member;
import com.example.boilerplate.sample.domain.entity.Todo;
import com.example.boilerplate.sample.domain.repository.MemberRepository;
import com.example.boilerplate.sample.domain.repository.TodoRepository;
import com.example.boilerplate.sample.dto.MemberDto;
import com.example.boilerplate.sample.dto.TodoDto;
import jakarta.persistence.EntityManager;
import java.util.Arrays;
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
@SpringBootTest
@ActiveProfiles("local")
class SampleServiceTest {

  private static final String NAME_TEST_STR = "test";
  private static final String EMAIL_PRE_TEST_STR = "test";
  private static final String TITLE_TEST_STR = "Title Test";
  private static final String DESCRIPTION_TEST_STR = "Description Test";
  private static final String FORMAT_PATTERN = "%02d";

  @Autowired
  SampleService sampleService;

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  TodoRepository todoRepository;

  @Autowired
  EntityManager entityManager;

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("회원 CRUD 테스트")
  @Nested
  class TestMember {

    @Order(1)
    @Transactional
    @DisplayName("getMemberList_회원 목록 조회")
    @Test
    void testGetMemberList() {

      // Given
      setUpMembers();
      List<String> sorts = Arrays.asList("email,desc", "name,asc");
      MemberDto.Request memberRequest = MemberDto.Request.builder()
          .name(NAME_TEST_STR)
          .email("gmail.com")
          .page(1)
          .size(10)
          .sorts(sorts)
          .build();

      // When
      Page<MemberDto.Response> memberResponses = sampleService.getMemberList(memberRequest);

      // Then
      log.debug("memberResponses:[{}]", memberResponses);
      assertFalse(memberResponses.isEmpty());
    }

    @Order(2)
    @Transactional
    @DisplayName("getMember_Member 상세 조회")
    @Test
    void testGetMember() {

      // Given
      Member member = getMemberAfterInsertTodos();

      // When
      MemberDto.Response memberResponse = sampleService.getMember(
          MemberDto.Request.builder()
              .id(member.getId())
              .build());
      log.debug("memberResponse : {}", memberResponse);

      // Then
      assertAll(
          () -> assertEquals(member.getName(), memberResponse.getName()),
          () -> assertEquals(member.getEmail(), memberResponse.getEmail())
      );
    }

    @Order(3)
    @Transactional
    @DisplayName("getMember_존재하지 않는 회원 정보를 조회")
    @Test
    void testGetMemberNotFound() {

      // Given
      Member member = getMemberAfterInsertTodos();
      Long notFoundMemberId = member.getId() + 1;
      member.setId(notFoundMemberId);

      // When
      ApiException apiException =
          assertThrows(ApiException.class, () -> sampleService.getMember(
              MemberDto.Request.builder()
                  .id(member.getId())
                  .build()));
      log.error("apiException memberId : {}", member.getId(), apiException);

      // Then
      assertAll(
          () -> assertEquals(ApiStatus.MEMBER_NOT_FOUND.getCode(),
              apiException.getStatus().getCode()),
          () -> assertEquals(ApiStatus.MEMBER_NOT_FOUND.getMessage(), apiException.getMessage())
      );
    }

    @Order(4)
    @Transactional
    @DisplayName("insertMember_회원 저장")
    @Test
    void testInsertMember() {

      // Given
      MemberDto.Request memberRequest = MemberDto.Request.builder()
          .name(NAME_TEST_STR)
          .email(EMAIL_PRE_TEST_STR + "@gmail.co.kr")
          .password("password1!")
          .build();

      // When
      MemberDto.Response memberResponse = sampleService.insertMember(memberRequest);
      log.debug("memberResponse : {}", memberResponse);

      // Then
      assertAll(
          () -> assertEquals(memberRequest.getName(), memberResponse.getName()),
          () -> assertEquals(memberRequest.getEmail(), memberResponse.getEmail())
      );
    }

    @Order(5)
    @Transactional
    @DisplayName("updateMember_회원 수정")
    @Test
    void testUpdateMember() {

      // Given
      Member member = getMemberAfterInsertTodos();
      MemberDto.Request memberRequest = MemberDto.Request.builder()
          .id(member.getId())
          .name("admin")
          .email("amdin@gmail.co.kr")
          .build();

      // When
      MemberDto.Response memberResponse = sampleService.updateMember(memberRequest);
      log.debug("memberResponse : {}", memberResponse);

      // Then
      assertAll(
          () -> assertEquals(memberRequest.getName(), memberResponse.getName()),
          () -> assertEquals(memberRequest.getEmail(), memberResponse.getEmail())
      );
    }
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("할 일 CRUD 테스트")
  @Nested
  class TestTodo {

    @Order(1)
    @Transactional
    @DisplayName("getTodoList_할 일 목록 조회")
    @Test
    void testGetTodos() {

      // Given
      setUpTodos();
      TodoDto.Request todoRequest = TodoDto.Request.builder()
          .title(TITLE_TEST_STR)
          .description(DESCRIPTION_TEST_STR)
          .completed(true)
          .build();

      // When
      List<TodoDto.Response> todoResponses = sampleService.getTodoList(todoRequest);
      log.debug("todoResponses : {}", todoResponses);

      // Then
      assertFalse(todoResponses.isEmpty());
    }

    @Order(2)
    @Transactional
    @DisplayName("getTodo_할 일 상세 조회")
    @Test
    void testGetTodo() {

      // Given
      Todo todo = getTodoAfterInsertTodo();

      // When
      TodoDto.Response todoResponse = sampleService.getTodo(
          TodoDto.Request.builder()
              .id(todo.getId())
              .build());
      log.debug("todoResponse : {}", todoResponse);

      // Then
      assertAll(
          () -> assertEquals(todo.getTitle(), todoResponse.getTitle()),
          () -> assertEquals(todo.getDescription(), todoResponse.getDescription()),
          () -> assertEquals(todo.getCompleted(), todoResponse.getCompleted())
      );
    }

    @Order(3)
    @Transactional
    @DisplayName("getTodo_존재하지 않는 할 일 정보를 조회")
    @Test
    void testGetTodoNotFound() {

      // Given
      Todo todo = getTodoAfterInsertTodo();
      Long notFoundTodoId = todo.getId() + 1;
      todo.setId(notFoundTodoId);

      // When
      ApiException apiException =
          assertThrows(ApiException.class, () -> sampleService.getTodo(
              TodoDto.Request.builder()
                  .id(todo.getId())
                  .build()));
      log.error("apiException todoId : {}", todo.getId(), apiException);

      // Then
      assertAll(
          () -> assertEquals(ApiStatus.TODO_NOT_FOUND.getCode(),
              apiException.getStatus().getCode()),
          () -> assertEquals(ApiStatus.TODO_NOT_FOUND.getMessage(), apiException.getMessage())
      );
    }

    @Order(4)
    @Transactional
    @DisplayName("insertTodo_할 일 저장")
    @Test
    void testInsertTodo() {

      // Given
      Long memberId = insertMember(NAME_TEST_STR, EMAIL_PRE_TEST_STR + "@gmail.co.kr");
      entityManager.flush();
      entityManager.clear();

      TodoDto.Request todoRequest = TodoDto.Request.builder()
          .title(TITLE_TEST_STR)
          .memberId(memberId)
          .description(DESCRIPTION_TEST_STR)
          .completed(false)
          .build();

      // When
      TodoDto.Response todoResponse = sampleService.insertTodo(todoRequest);
      log.debug("todoResponse : {}", todoResponse);

      // Then
      assertAll(
          () -> assertEquals(todoRequest.getTitle(), todoResponse.getTitle()),
          () -> assertEquals(todoRequest.getDescription(), todoResponse.getDescription()),
          () -> assertEquals(todoRequest.getCompleted(), todoResponse.getCompleted())
      );
    }

    @Order(5)
    @Transactional
    @DisplayName("updateTodo_할 일 수정")
    @Test
    void testUpdateTodo() {

      // Given
      Todo todo = getTodoAfterInsertTodo();
      Long todoId = todo.getId();
      TodoDto.Response insertTodo = sampleService.getTodo(
          TodoDto.Request.builder()
              .id(todo.getId())
              .build());
      log.debug("insertTodo : {}", insertTodo);

      // When
      sampleService.updateTodo(
          TodoDto.Request.builder()
              .id(todoId)
              .title("Title Test Update01")
              .description("Description Test Update01")
              .completed(true)
              .build());
      entityManager.flush();
      entityManager.clear();
      TodoDto.Response updateTodo = sampleService.getTodo(
          TodoDto.Request.builder()
              .id(todoId)
              .build());
      log.debug("updateTodo : {}", updateTodo);

      // Then
      assertAll(
          () -> assertEquals(insertTodo.getId(), updateTodo.getId()),
          () -> assertNotEquals(insertTodo.getTitle(), updateTodo.getTitle()),
          () -> assertNotEquals(insertTodo.getDescription(), updateTodo.getDescription()),
          () -> assertNotEquals(insertTodo.getCompleted(), updateTodo.getCompleted())
      );
    }

    @Order(6)
    @Transactional
    @DisplayName("updateTodoDynamic_할 일 수정")
    @Test
    void testUpdateTodoDynamic() {

      // Given
      Todo todo = getTodoAfterInsertTodo();
      Long todoId = todo.getId();
      TodoDto.Response todoDetail01 = sampleService.getTodo(
          TodoDto.Request.builder()
              .id(todoId)
              .build());
      log.debug("todoDetail01:[{}]", todoDetail01);

      // When
      sampleService.updateTodoCompleted(
          TodoDto.Request.builder()
              .id(todoId)
              .completed(true)
              .build());
      entityManager.flush();
      entityManager.clear();
      TodoDto.Response todoDetail02 = sampleService.getTodo(
          TodoDto.Request.builder()
              .id(todoId)
              .build());
      log.debug("todoDetail02:[{}]", todoDetail02);

      // Then
      assertAll(
          () -> assertEquals(todoDetail01.getId(), todoDetail02.getId()),
          () -> assertEquals(todoDetail01.getTitle(), todoDetail02.getTitle()),
          () -> assertEquals(todoDetail01.getDescription(), todoDetail02.getDescription()),
          () -> assertNotEquals(todoDetail01.getCompleted(), todoDetail02.getCompleted())
      );
    }
  }

  /**
   * 회원 저장
   */
  @Disabled
  Long insertMember(
      String name,
      String email) {
    return memberRepository.save(
        Member.builder()
            .name(name)
            .email(email)
            .build()
    ).getId();
  }

  /**
   * 할 일 저장
   */
  @Disabled
  void insertTodo(
      Long memberId,
      String title,
      String description,
      Boolean completed) {
    todoRepository.save(
        Todo.builder()
            .member(
                Member.builder()
                    .id(memberId)
                    .build())
            .title(title)
            .description(description)
            .completed(completed)
            .build()
    );
  }

  /**
   * Member 목록 설정
   */
  @Disabled
  void setUpMembers() {

    for (int a = 1; a <= 100; a++) {
      Long memberId =
          insertMember(NAME_TEST_STR + String.format(FORMAT_PATTERN, a),
              (a % 2 == 0)
                  ? EMAIL_PRE_TEST_STR + String.format(FORMAT_PATTERN, a) + "@naver.com"
                  : EMAIL_PRE_TEST_STR + String.format(FORMAT_PATTERN, a) + "@gmail.com");
      for (int b = 1; b <= 5; b++) {
        insertTodo(
            memberId,
            TITLE_TEST_STR + String.format(FORMAT_PATTERN, b),
            DESCRIPTION_TEST_STR + String.format(FORMAT_PATTERN, b),
            b % 2 == 0);
      }
    }

    entityManager.flush();
    entityManager.clear();
  }

  /**
   * 할 일 목록을 설정
   */
  @Disabled
  void setUpTodos() {

    Long memberId01 = insertMember(NAME_TEST_STR + "01", EMAIL_PRE_TEST_STR + "01@gmail.com");
    Long memberId02 = insertMember(NAME_TEST_STR + "02", EMAIL_PRE_TEST_STR + "02@naver.com");
    Long memberId03 = insertMember(NAME_TEST_STR + "03", EMAIL_PRE_TEST_STR + "03@daum.net");

    for (int a = 1; a <= 100; a++) {
      Long memberId = (a % 3 == 0) ? memberId03 : (a % 2 == 0) ? memberId02 : memberId01;
      insertTodo(memberId,
          TITLE_TEST_STR + String.format(FORMAT_PATTERN, a),
          DESCRIPTION_TEST_STR + String.format(FORMAT_PATTERN, a),
          a % 2 == 0);
    }

    entityManager.flush();
    entityManager.clear();
  }

  /**
   * 회원 1명, 할 일 10개를 저장한 후 회원을 반환
   */
  @Disabled
  Member getMemberAfterInsertTodos() {

    Member member = memberRepository.save(
        Member.builder()
            .name(NAME_TEST_STR + "01")
            .email(EMAIL_PRE_TEST_STR + "01@gmail.com")
            .build());

    for (int a = 1; a <= 10; a++) {
      insertTodo(
          member.getId(),
          TITLE_TEST_STR + String.format(FORMAT_PATTERN, a),
          DESCRIPTION_TEST_STR + String.format(FORMAT_PATTERN, a),
          a % 2 == 0);
    }

    entityManager.flush();
    entityManager.clear();

    return member;
  }

  /**
   * 회원 1명, 할 일 1개를 저장한 후 할 일을 반환
   */
  @Disabled
  Todo getTodoAfterInsertTodo() {

    Member member = memberRepository.save(
        Member.builder()
            .name(NAME_TEST_STR)
            .email(EMAIL_PRE_TEST_STR + "@gmail.com")
            .build());

    Todo todo = todoRepository.save(
        Todo.builder()
            .member(member)
            .title("Title Test Insert")
            .description("Description Test Insert")
            .completed(false)
            .build());

    entityManager.flush();
    entityManager.clear();

    return todo;
  }
}
