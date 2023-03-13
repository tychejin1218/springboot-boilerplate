package com.example.boilerplate.sample.domain.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.boilerplate.sample.domain.entity.Member;
import com.example.boilerplate.sample.domain.entity.Todo;
import java.util.List;
import javax.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@ActiveProfiles("local")
@Disabled
class TodoQuerydslTest {

  @Autowired
  private TodoRepository todoRepository;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  EntityManager entityManager;

  @Transactional
  @DisplayName("getTodos_QueryDSL을 사용하여 To-Do 목록 조회")
  @Test
  void testGetTodos() {

    // Given
    setUpTodos();

    Todo todo = Todo.builder()
        .title("Title Test")
        .description("Description Test")
        .completed(false)
        .build();

    // When
    List<Todo> todos = todoRepository.getTodos(todo);

    // Then
    log.debug("todos:[{}]", todos);
    assertTrue(!todos.isEmpty());
  }

  @Transactional
  @DisplayName("getTodos_Pageable을 사용하여 To-Do 목록 조회")
  @Test
  void testGetTodosPageable() {

    // Given
    setUpTodos();

    Todo todo = Todo.builder()
        .title("Title Test")
        .description("Description Test")
        .completed(true)
        .build();

    // When
    Page<Todo> todoPage = todoRepository.getTodos(todo, PageRequest.of(0, 5));

    // Then
    log.debug("Content:[{}]", todoPage.getContent());
    log.debug("Size:[{}]", todoPage.getSize());
    log.debug("TotalPages:[{}]", todoPage.getTotalPages());
    log.debug("TotalElements:[{}]", todoPage.getTotalElements());
    log.debug("NextPageable:[{}]", todoPage.nextPageable());
    assertTrue(!todoPage.isEmpty());
  }

  /**
   * Member를 저장
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
   * To-Do를 저장
   */
  @Disabled
  void insertTodo(
      Long memberId,
      String title,
      String description,
      Boolean completed) {
    todoRepository.save(
        Todo.builder()
            .member(Member.builder()
                .id(memberId)
                .build())
            .title(title)
            .description(description)
            .completed(completed)
            .build()
    );
  }

  /**
   * To-Do 목록을 설정
   */
  @Disabled
  void setUpTodos() {

    Long memberId01 = insertMember("test01", "test01@naver.com");
    Long memberId02 = insertMember("test02", "test02@gmail.com");
    Long memberId03 = insertMember("test03", "test03@naver.com");

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
}
