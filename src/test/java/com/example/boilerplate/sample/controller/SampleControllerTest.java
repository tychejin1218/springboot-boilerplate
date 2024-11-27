package com.example.boilerplate.sample.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.boilerplate.common.reponse.BaseResponse;
import com.example.boilerplate.common.type.ApiStatus;
import com.example.boilerplate.domain.entity.MemberEntity;
import com.example.boilerplate.domain.entity.TodoEntity;
import com.example.boilerplate.domain.repository.MemberRepository;
import com.example.boilerplate.domain.repository.TodoRepository;
import com.example.boilerplate.sample.dto.MemberDto;
import com.example.boilerplate.sample.dto.TodoDto;
import com.example.boilerplate.sign.dto.SignDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
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
class SampleControllerTest {

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String AUTHORIZATION_TOKEN_PREFIX = "Bearer ";

  private static final String NAME_TEST_STR = "test";
  private static final String EMAIL_PRE_TEST_STR = "test";
  private static final String TITLE_TEST_STR = "Title Test";
  private static final String DESCRIPTION_TEST_STR = "Description Test";
  private static final String FORMAT_PATTERN = "%02d";

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  TodoRepository todoRepository;

  @Autowired
  EntityManager entityManager;

  private static String token;

  @BeforeAll
  static void setUp(
      @Autowired MockMvc mockMvc,
      @Autowired ObjectMapper objectMapper) throws Exception {

    SignDto.Request signInRequest = SignDto.Request.builder()
        .email("admin@naver.com")
        .password("password1!")
        .build();

    ResultActions signInActions = mockMvc.perform(
        post("/sign/signin")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(signInRequest))
    );
    signInActions.andDo(print());

    String jsonResponse = signInActions.andReturn().getResponse().getContentAsString();
    BaseResponse<SignDto.Response> baseResponse = objectMapper.readValue(jsonResponse,
        new TypeReference<>() {
        });
    token = baseResponse.getData().getToken();
  }

  @DisplayName("회원 CRUD 테스트")
  @Nested
  class TestMember {

    @Transactional
    @DisplayName("getMemberList_회원 목록 조회_statusCode:200")
    @Test
    void testGetMemberList() throws Exception {

      // Given
      setUpMembers();
      String url = "/sample/members";
      List<String> sorts = List.of("email,desc", "name,asc");
      MemberDto.Request memberRequest = MemberDto.Request.builder()
          .name("test")
          .email("test")
          .page(1)
          .size(10)
          .sorts(sorts)
          .build();
      String queryParams = String.format("?name=%s&email=%s&page=%d&size=%d&sorts=%s&sorts=%s",
          memberRequest.getName(), memberRequest.getEmail(), memberRequest.getPage(),
          memberRequest.getSize(), memberRequest.getSorts().get(0),
          memberRequest.getSorts().get(1));

      // When
      ResultActions resultActions = mockMvc.perform(
          get(url + queryParams)
              .contentType(MediaType.APPLICATION_JSON)
              .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN_PREFIX + token)
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
    @DisplayName("getMember_회원 조회_statusCode:200")
    @Test
    void testGetMember() throws Exception {

      // Given
      MemberEntity member = getMemberAfterInsertTodos();
      String url = "/sample/member/" + member.getId();

      // When
      ResultActions resultActions = mockMvc.perform(
          get(url)
              .contentType(MediaType.APPLICATION_JSON)
              .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN_PREFIX + token)
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
    @DisplayName("getMember_회원이 존재하지 않는 경우_statusCode:807")
    @Test
    void testGetMemberNotFound() throws Exception {

      // Given
      MemberEntity member = getMemberAfterInsertTodos();
      String url = "/sample/member/" + member.getId() + 1;

      // When
      ResultActions resultActions = mockMvc.perform(
          get(url)
              .contentType(MediaType.APPLICATION_JSON)
              .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN_PREFIX + token)
      );

      // Then
      resultActions
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.statusCode").value(ApiStatus.MEMBER_NOT_FOUND.getCode()))
          .andExpect(jsonPath("$.message").value(ApiStatus.MEMBER_NOT_FOUND.getMessage()))
          .andExpect(jsonPath("method").value(HttpMethod.GET.toString()))
          .andExpect(jsonPath("timestamp").isNotEmpty())
          .andDo(print());
    }

    @Transactional
    @DisplayName("insertMember_회원 저장_statusCode:200")
    @Test
    void testInsertMember() throws Exception {

      // Given
      String url = "/sample/insert/member";
      MemberDto.Request memberRequest = MemberDto.Request.builder()
          .name("test")
          .email("test@gmail.co.kr")
          .password("password1!")
          .build();

      // When
      ResultActions resultActions = mockMvc.perform(
          post(url)
              .contentType(MediaType.APPLICATION_JSON)
              .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN_PREFIX + token)
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
    @DisplayName("updateMember_회원 수정_statusCode:200")
    @Test
    void testUpdateMember() throws Exception {

      // Given
      MemberEntity member = getMemberAfterInsertTodos();
      String url = "/sample/update/member";
      MemberDto.Request memberRequest = MemberDto.Request.builder()
          .id(member.getId())
          .name("admin")
          .email("admin@gmail.co.kr")
          .build();

      // When
      ResultActions resultActions = mockMvc.perform(
          put(url)
              .contentType(MediaType.APPLICATION_JSON)
              .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN_PREFIX + token)
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

  @DisplayName("할 일 CRUD 테스트")
  @Nested
  class TestTodo {

    @Transactional
    @DisplayName("getTodos_할 일 목록 조회_statusCode:200")
    @Test
    void testGetTodos() throws Exception {

      // Given
      setUpTodos();
      String url = "/sample/todos";
      List<String> sorts = List.of("id,desc");
      TodoDto.Request todoRequest = TodoDto.Request.builder()
          .title("test")
          .description("test")
          .completed(true)
          .page(1)
          .size(10)
          .sorts(sorts)
          .build();

      String queryParams = String.format(
          "?title=%s&description=%s&completed=%b&page=%d&size=%d&sorts=%s",
          todoRequest.getTitle(),
          todoRequest.getDescription(),
          todoRequest.getCompleted(),
          todoRequest.getPage(),
          todoRequest.getSize(),
          String.join("&sorts=", todoRequest.getSorts())
      );

      // When
      ResultActions resultActions = mockMvc.perform(
          get(url + queryParams)
              .contentType(MediaType.APPLICATION_JSON)
              .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN_PREFIX + token)
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
    @DisplayName("getTodo_할 일 조회_statusCode:200")
    @Test
    void testGetTodo() throws Exception {

      // Given
      TodoEntity todo = getTodoAfterInsertTodo();
      Long todoId = todo.getId();
      String url = "/sample/todo/" + todoId;

      // When
      ResultActions resultActions = mockMvc.perform(
          get(url)
              .contentType(MediaType.APPLICATION_JSON)
              .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN_PREFIX + token)
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
    @DisplayName("getTodo_할 일이 존재하지 않는 경우_statusCode:808")
    @Test
    void testGetTodoNotFound() throws Exception {

      // Given
      TodoEntity todo = getTodoAfterInsertTodo();
      Long todoId = todo.getId();
      String url = "/sample/todo/" + todoId + 1;

      // When
      ResultActions resultActions = mockMvc.perform(
          get(url)
              .contentType(MediaType.APPLICATION_JSON)
              .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN_PREFIX + token)
      );

      // Then
      resultActions
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.statusCode").value(ApiStatus.TODO_NOT_FOUND.getCode()))
          .andExpect(jsonPath("$.message").value(ApiStatus.TODO_NOT_FOUND.getMessage()))
          .andExpect(jsonPath("method").value(HttpMethod.GET.toString()))
          .andExpect(jsonPath("timestamp").isNotEmpty())
          .andDo(print());
    }

    @Transactional
    @DisplayName("insertTodo_할 일 저장_statusCode:200")
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
              .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN_PREFIX + token)
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
    @DisplayName("updateTodo_할 일 수정_statusCode:200")
    @Test
    void testUpdateTodo() throws Exception {

      // Given
      TodoEntity todo = getTodoAfterInsertTodo();

      String url = "/sample/update/todo";
      TodoDto.Request todoRequest = TodoDto.Request.builder()
          .id(todo.getId())
          .title("Title Test Update")
          .description("Description Test Update")
          .completed(true)
          .build();

      // When
      ResultActions resultActions = mockMvc.perform(
          put(url)
              .contentType(MediaType.APPLICATION_JSON)
              .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN_PREFIX + token)
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
   * 회원 저장
   */
  @Disabled
  Long insertMember(
      String name,
      String email) {
    return memberRepository.save(
        MemberEntity.builder()
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
        TodoEntity.builder()
            .member(
                MemberEntity.builder()
                    .id(memberId)
                    .build())
            .title(title)
            .description(description)
            .completed(completed)
            .build()
    );
  }

  /**
   * 회원 목록 설정
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
   * To-Do 목록 설정
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
  MemberEntity getMemberAfterInsertTodos() {

    MemberEntity member = memberRepository.save(
        MemberEntity.builder()
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
  TodoEntity getTodoAfterInsertTodo() {

    MemberEntity member = memberRepository.save(
        MemberEntity.builder()
            .name(NAME_TEST_STR)
            .email(EMAIL_PRE_TEST_STR + "@gmail.com")
            .build());

    TodoEntity todo = todoRepository.save(
        TodoEntity.builder()
            .member(member)
            .title("To-Do Title")
            .description("To-Do Description")
            .completed(false)
            .build());

    entityManager.flush();
    entityManager.clear();

    return todo;
  }
}
