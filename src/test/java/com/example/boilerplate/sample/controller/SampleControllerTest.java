package com.example.boilerplate.sample.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.boilerplate.common.type.ApiStatus;
import com.example.boilerplate.sample.domain.entity.Member;
import com.example.boilerplate.sample.domain.entity.Todo;
import com.example.boilerplate.sample.domain.repository.MemberRepository;
import com.example.boilerplate.sample.domain.repository.TodoRepository;
import com.example.boilerplate.sample.dto.MemberDto;
import com.example.boilerplate.sample.dto.TodoDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings({
    "PMD.AvoidDuplicateLiterals"
})
@Slf4j
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("local")
@Disabled
class SampleControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private TodoRepository todoRepository;

  @Autowired
  EntityManager entityManager;

  @DisplayName("Member CRUD 테스트")
  @Nested
  class TestMember {

    @Transactional
    @DisplayName("getMembers_Member 목록 조회_statusCode:200")
    @Test
    void testGetMembers() throws Exception {

      // Given
      setUpMembers();
      String url = "/sample/members";
      List<String> sorts = Arrays.asList("email,desc", "name,asc");
      MemberDto.Request memberRequest = MemberDto.Request.builder()
          .name("test")
          .email("test")
          .page(1)
          .size(10)
          .sorts(sorts)
          .build();

      // When
      ResultActions resultActions = mockMvc.perform(
          post(url)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(memberRequest))
      );

      // Then
      resultActions
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.statusCode").value(ApiStatus.OK.getCode()))
          .andExpect(jsonPath("$.message").value(ApiStatus.OK.getMessage()))
          .andExpect(jsonPath("$.data").isNotEmpty())
          .andDo(print());
    }

    @Transactional
    @DisplayName("getMember_Member 상세 조회_statusCode:200")
    @Test
    void testGetMember() throws Exception {

      // Given
      Member member = getMemberAfterInsertTodos();
      String url = "/sample/member";
      MemberDto.Request memberRequest = MemberDto.Request.builder()
          .id(member.getId())
          .build();

      // When
      ResultActions resultActions = mockMvc.perform(
          post(url)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(memberRequest))
      );

      // Then
      resultActions
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.statusCode").value(ApiStatus.OK.getCode()))
          .andExpect(jsonPath("$.message").value(ApiStatus.OK.getMessage()))
          .andExpect(jsonPath("$.data").isNotEmpty())
          .andDo(print());
    }

    @Transactional
    @DisplayName("getMember_Member가 존재하지 않는 경우_statusCode:804")
    @Test
    void testGetMemberNotFound() throws Exception {

      // Given
      Member member = getMemberAfterInsertTodos();
      String url = "/sample/member";
      MemberDto.Request memberRequest = MemberDto.Request.builder()
          .id(member.getId() + 1)
          .build();

      // When
      ResultActions resultActions = mockMvc.perform(
          post(url)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(memberRequest))
      );

      // Then
      resultActions
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.statusCode").value(
              ApiStatus.NOT_FOUND.getCode()))
          .andExpect(jsonPath("$.message").value(
              "존재하지 않는 Member 정보입니다."))
          .andExpect(jsonPath("method").value(HttpMethod.POST.toString()))
          .andExpect(jsonPath("timestamp").isNotEmpty())
          .andDo(print());
    }

    @Transactional
    @DisplayName("insertMember_Member 저장_statusCode:200")
    @Test
    void testInsertMember() throws Exception {

      // Given
      String url = "/sample/insert/member";
      MemberDto.Request memberRequest = MemberDto.Request.builder()
          .name("test")
          .email("test@gmail.co.kr")
          .build();

      // When
      ResultActions resultActions = mockMvc.perform(
          post(url)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(memberRequest))
      );

      // Then
      resultActions
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.statusCode").value(ApiStatus.OK.getCode()))
          .andExpect(jsonPath("$.message").value(ApiStatus.OK.getMessage()))
          .andExpect(jsonPath("$.data").isNotEmpty())
          .andDo(print());
    }

    @Transactional
    @DisplayName("updateMember_Member 수정_statusCode:200")
    @Test
    void testUpdateMember() throws Exception {

      // Given
      Member member = getMemberAfterInsertTodos();
      String url = "/sample/update/member";
      MemberDto.Request memberRequest = MemberDto.Request.builder()
          .id(member.getId())
          .name("admin")
          .email("admin@gmail.co.kr")
          .build();

      // When
      ResultActions resultActions = mockMvc.perform(
          post(url)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(memberRequest))
      );

      // Then
      resultActions
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.statusCode").value(ApiStatus.OK.getCode()))
          .andExpect(jsonPath("$.message").value(ApiStatus.OK.getMessage()))
          .andExpect(jsonPath("$.data").isNotEmpty())
          .andDo(print());
    }
  }


  @DisplayName("To-Do CRUD 테스트")
  @Nested
  class TestTodo {

    @Transactional
    @DisplayName("getTodos_To-Do 목록 조회_statusCode:200")
    @Test
    void testGetTodos() throws Exception {

      // Given
      setUpTodos();
      String url = "/sample/todos";
      List<String> sorts = Arrays.asList("id,desc");
      TodoDto.Request todoRequest = TodoDto.Request.builder()
          .title("test")
          .description("test")
          .completed(true)
          .page(1)
          .size(10)
          .sorts(sorts)
          .build();

      // When
      ResultActions resultActions = mockMvc.perform(
          post(url)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(todoRequest))
      );

      // Then
      resultActions
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.statusCode").value(ApiStatus.OK.getCode()))
          .andExpect(jsonPath("$.message").value(ApiStatus.OK.getMessage()))
          .andExpect(jsonPath("$.data").isNotEmpty())
          .andDo(print());
    }

    @Transactional
    @DisplayName("getTodo_To-Do 상세 조회_statusCode:200")
    @Test
    void testGetTodo() throws Exception {

      // Given
      Todo todo = getTodoAfterInsertTodo();
      Long todoId = todo.getId();
      String url = "/sample/todo";
      TodoDto.Request todoRequest = TodoDto.Request.builder()
          .id(todoId)
          .build();

      // When
      ResultActions resultActions = mockMvc.perform(
          post(url)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(todoRequest))
      );

      // Then
      resultActions
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.statusCode").value(ApiStatus.OK.getCode()))
          .andExpect(jsonPath("$.message").value(ApiStatus.OK.getMessage()))
          .andExpect(jsonPath("$.data").isNotEmpty())
          .andDo(print());
    }

    @Transactional
    @DisplayName("getTodo_To-Do가 존재하지 않는 경우_statusCode:804")
    @Test
    void testGetTodoNotFound() throws Exception {

      // Given
      Todo todo = getTodoAfterInsertTodo();
      Long todoId = todo.getId();
      String url = "/sample/todo";
      TodoDto.Request todoRequest = TodoDto.Request.builder()
          .id(todoId + 1)
          .build();

      // When
      ResultActions resultActions = mockMvc.perform(
          post(url)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(todoRequest))
      );

      // Then
      resultActions
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.statusCode").value(
              ApiStatus.NOT_FOUND.getCode()))
          .andExpect(jsonPath("$.message").value(
              "존재하지 않는 To-Do 정보입니다."))
          .andExpect(jsonPath("method").value(HttpMethod.POST.toString()))
          .andExpect(jsonPath("timestamp").isNotEmpty())
          .andDo(print());
    }

    @Transactional
    @DisplayName("insertTodo_To-Do 저장_statusCode:200")
    @Test
    void testInsertTodo() throws Exception {

      // Given
      Long memberId = insertMember("test", "test@gmail.co.kr");
      entityManager.flush();
      entityManager.clear();

      String url = "/sample/insert/todo";
      TodoDto.Request todoRequest = TodoDto.Request.builder()
          .title("Title Test")
          .memberId(memberId)
          .description("Description Test")
          .completed(false)
          .build();

      // When
      ResultActions resultActions = mockMvc.perform(
          post(url)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(todoRequest))
      );

      // Then
      resultActions
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.statusCode").value(ApiStatus.OK.getCode()))
          .andExpect(jsonPath("$.message").value(ApiStatus.OK.getMessage()))
          .andExpect(jsonPath("$.data").isNotEmpty())
          .andDo(print());
    }

    @Transactional
    @DisplayName("updateTodo_To-Do 수정_statusCode:200")
    @Test
    void testUpdateTodo() throws Exception {

      // Given
      Todo todo = getTodoAfterInsertTodo();

      String url = "/sample/update/todo";
      TodoDto.Request todoRequest = TodoDto.Request.builder()
          .id(todo.getId())
          .title("Title Test Update")
          .description("Description Test Update")
          .completed(true)
          .build();

      // When
      ResultActions resultActions = mockMvc.perform(
          post(url)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(todoRequest))
      );

      // Then
      resultActions
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.statusCode").value(ApiStatus.OK.getCode()))
          .andExpect(jsonPath("$.message").value(ApiStatus.OK.getMessage()))
          .andExpect(jsonPath("$.data").isNotEmpty())
          .andDo(print());
    }
  }

  /**
   * Member 저장
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
   * To-Do 저장
   */
  @Disabled
  Long insertTodo(
      Long memberId,
      String title,
      String description,
      Boolean completed) {
    return todoRepository.save(
        Todo.builder()
            .member(Member.builder()
                .id(memberId)
                .build())
            .title(title)
            .description(description)
            .completed(completed)
            .build()
    ).getId();
  }

  /**
   * Member 목록 설정
   */
  @Disabled
  void setUpMembers() {

    Long memberId;
    String name;
    String email;

    String title;
    String description;
    Boolean completed;

    for (int a = 1; a <= 10; a++) {

      name = "test" + String.format("%02d", a);
      if (a % 2 == 0) {
        email = "test@naver.com";
      } else {
        email = "test@gmail.com";
      }
      memberId = insertMember(name, email);

      for (int b = 1; b <= 5; b++) {
        title = "Title Test" + String.format("%02d", b);
        description = "Description Test" + String.format("%02d", b);
        if (b % 2 == 0) {
          completed = true;
        } else {
          completed = false;
        }
        insertTodo(memberId, title, description, completed);
      }
    }

    entityManager.flush();
    entityManager.clear();
  }

  /**
   * To-Do 목록 설정
   */
  @Disabled
  void setUpTodos() {

    Long memberId01 = insertMember("test01", "test01@gmail.com");
    Long memberId02 = insertMember("test02", "test02@naver.com");
    Long memberId03 = insertMember("test03", "test03@daum.net");

    Long memberId;
    String title;
    String description;
    Boolean completed;

    for (int a = 1; a <= 100; a++) {

      if (a % 3 == 0) {
        memberId = memberId03;
      } else if (a % 2 == 0) {
        memberId = memberId02;
      } else {
        memberId = memberId01;
      }

      title = "Title Test" + String.format("%02d", a);
      description = "Description Test" + String.format("%02d", a);
      if (a % 2 == 0) {
        completed = true;
      } else {
        completed = false;
      }
      insertTodo(memberId, title, description, completed);
    }

    entityManager.flush();
    entityManager.clear();
  }

  /**
   * Member 1개, To-Do 10개를 저장한 후 Member를 반환
   */
  @Disabled
  Member getMemberAfterInsertTodos() {

    Member member = memberRepository.save(
        Member.builder()
            .name("test01")
            .email("test01@gmail.com")
            .build());

    String title;
    String description;
    Boolean completed;

    for (int a = 1; a <= 10; a++) {

      title = "Title Test" + String.format("%02d", a);
      description = "Description Test" + String.format("%02d", a);
      if (a % 2 == 0) {
        completed = true;
      } else {
        completed = false;
      }
      insertTodo(member.getId(), title, description, completed);
    }

    entityManager.flush();
    entityManager.clear();

    return member;
  }

  /**
   * Member, To-Do를 1개씩 저장한 후 Todo를 반환
   */
  @Disabled
  Todo getTodoAfterInsertTodo() {

    Member member = memberRepository.save(
        Member.builder()
            .name("test")
            .email("test@gmail.com")
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
