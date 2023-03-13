package com.example.boilerplate.sample.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.boilerplate.common.exception.ApiException;
import com.example.boilerplate.common.type.ApiStatus;
import com.example.boilerplate.sample.domain.entity.Member;
import com.example.boilerplate.sample.domain.entity.Todo;
import com.example.boilerplate.sample.domain.mapstruct.MemberMapStruct;
import com.example.boilerplate.sample.domain.repository.MemberRepository;
import com.example.boilerplate.sample.domain.repository.TodoRepository;
import com.example.boilerplate.sample.dto.MemberDto;
import com.example.boilerplate.sample.dto.TodoDto;
import java.util.List;
import javax.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@ActiveProfiles("local")
@Disabled
class SampleServiceTest {

  @Autowired
  private SampleService sampleService;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private MemberMapStruct memberMapStruct;

  @Autowired
  private TodoRepository todoRepository;

  @Autowired
  EntityManager entityManager;

  @Transactional
  @DisplayName("getMembers_Member 목록 조회")
  @Test
  void testGetMembers() {

    // Given
    setUpMembers();
    MemberDto.Request memberRequest = MemberDto.Request.builder()
        .name("test")
        .email("gmail.com")
        .build();

    // When
    List<MemberDto.Response> memberResponses = sampleService.getMembers(memberRequest);

    // Then
    log.debug("memberResponses:[{}]", memberResponses);
    assertTrue(!memberResponses.isEmpty());
  }

  @Transactional
  @DisplayName("getTodos_To-Do 목록 조회")
  @Test
  void testGetTodos() {

    // Given
    setUpTodos();
    TodoDto.Request todoRequest = TodoDto.Request.builder()
        .title("Title Test")
        .description("Description Test")
        .completed(true)
        .build();

    // When
    List<TodoDto.Response> todoResponses = sampleService.getTodos(todoRequest);

    // Then
    log.debug("todoResponses:[{}]", todoResponses);
    assertTrue(!todoResponses.isEmpty());
  }

  @Transactional
  @DisplayName("getMember_Member 상세 조회")
  @Test
  void testGetMember() {

    // Given
    Member member = getMemberAfterInsertTodos();

    // When
    MemberDto.Response memberResponse = sampleService.getMember(member.getId());

    // Then
    log.debug("memberResponse:[{}]", memberResponse);
    assertAll(
        () -> assertEquals(member.getName(), memberResponse.getName()),
        () -> assertEquals(member.getEmail(), memberResponse.getEmail())
    );
  }

  @Transactional
  @DisplayName("getMember_존재하지 않는 Member 정보를 조회")
  @Test
  void testGetMemberNotFound() {

    // Given
    Member member = getMemberAfterInsertTodos();
    Long notFoundMemberId = member.getId() + 1;
    member.setId(notFoundMemberId);

    // When
    ApiException apiException =
        assertThrows(ApiException.class, () -> sampleService.getMember(member.getId()));

    // Then
    log.debug("apiException:[{}]", apiException);
    assertAll(
        () -> assertEquals(ApiStatus.NOT_FOUND.getCode(), apiException.getStatus().getCode()),
        () -> assertEquals("존재하지 않는 Member 정보입니다.", apiException.getMessage())
    );
  }

  @Transactional
  @DisplayName("getTodo_To-Do 상세 조회")
  @Test
  void testGetTodo() {

    // Given
    Todo todo = getTodoAfterInsertTodo();

    // When
    TodoDto.Response todoResponse = sampleService.getTodo(todo.getId());

    // Then
    log.debug("todoResponse:[{}]", todoResponse);
    assertAll(
        () -> assertEquals(todo.getTitle(), todoResponse.getTitle()),
        () -> assertEquals(todo.getDescription(), todoResponse.getDescription()),
        () -> assertEquals(todo.getCompleted(), todoResponse.getCompleted())
    );
  }

  @Transactional
  @DisplayName("getMember_존재하지 않는 To-DO 정보를 조회")
  @Test
  void testGetTodoNotFound() {

    // Given
    Todo todo = getTodoAfterInsertTodo();
    Long notFoundTodoId = todo.getId() + 1;
    todo.setId(notFoundTodoId);

    // When
    ApiException apiException =
        assertThrows(ApiException.class, () -> sampleService.getTodo(todo.getId()));

    // Then
    log.debug("apiException:[{}]", apiException);
    assertAll(
        () -> assertEquals(ApiStatus.NOT_FOUND.getCode(), apiException.getStatus().getCode()),
        () -> assertEquals("존재하지 않는 To-Do 정보입니다.", apiException.getMessage())
    );
  }

  @Transactional
  @DisplayName("insertMember_Member 저장")
  @Test
  void testInsertMember() {

    // Given
    MemberDto.Request memberRequest = MemberDto.Request.builder()
        .name("test")
        .email("test@gmail.co.kr")
        .build();

    // When
    MemberDto.Response memberResponse = sampleService.insertMember(memberRequest);

    // Then
    log.debug("memberResponse:[{}]", memberResponse);
    assertAll(
        () -> assertEquals(memberRequest.getName(), memberResponse.getName()),
        () -> assertEquals(memberRequest.getEmail(), memberResponse.getEmail())
    );
  }

  @Transactional
  @DisplayName("insertTodo_To-Do 저장")
  @Test
  void testInsertTodo() {

    // Given
    Long memberId = insertMember("test", "test@gmail.co.kr");
    entityManager.flush();
    entityManager.clear();

    TodoDto.Request todoRequest = TodoDto.Request.builder()
        .title("Title Test")
        .memberId(memberId)
        .description("Description Test")
        .completed(false)
        .build();

    // When
    TodoDto.Response todoResponse = sampleService.insertTodo(todoRequest);

    // Then
    log.debug("todoResponse:[{}]", todoResponse);
    assertAll(
        () -> assertEquals(todoRequest.getTitle(), todoResponse.getTitle()),
        () -> assertEquals(todoRequest.getDescription(), todoResponse.getDescription()),
        () -> assertEquals(todoRequest.getCompleted(), todoResponse.getCompleted())
    );
  }

  @Transactional
  @DisplayName("updateMember_Member 수정")
  @Test
  void testUpdateMember() {

    // Given
    Member member = memberRepository.save(
        Member.builder()
            //.password("1234")
            .name("test01")
            .email("test01@gmail.com")
            .build());
    memberRepository.save(member);

    entityManager.flush();
    entityManager.clear();

    MemberDto.Request memberRequest =
        MemberDto.Request.builder()
            .id(member.getId())
            .name("test02")
            .email("test02@naver.com")
            .build();

    // When
    MemberDto.Response memberResponse = sampleService.updateMemberWithDynamicUpdate(memberRequest);
    log.debug("memberResponse:[{}]", memberResponse);

    entityManager.flush();
    entityManager.clear();

    // Then
    MemberDto.Response memberDetail = sampleService.getMember(member.getId());
    log.debug("memberDetail:[{}]", memberDetail);
    assertAll(
        () -> assertEquals(memberRequest.getId(), memberDetail.getId()),
        () -> assertEquals(member.getName(), memberDetail.getName()),
        () -> assertEquals(memberRequest.getEmail(), memberDetail.getEmail())
    );
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

    for (int a = 1; a <= 100; a++) {

      name = "test" + String.format("%02d", a);
      if (a % 2 == 0) {
        email = name + "@naver.com";
      } else {
        email = name + "@gmail.com";
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
   * To-Do 목록을 설정
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
            .name("test01")
            .email("test01@gmail.com")
            .build());

    Todo todo = todoRepository.save(
        Todo.builder()
            .member(member)
            .title("Title Test Insert01")
            .description("Description Test Insert01")
            .completed(false)
            .build());

    entityManager.flush();
    entityManager.clear();

    return todo;
  }
}
