package com.example.boilerplate.sample.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;

import com.example.boilerplate.domain.entity.MemberEntity;
import com.example.boilerplate.domain.entity.TodoEntity;
import com.example.boilerplate.domain.repository.MemberRepository;
import com.example.boilerplate.domain.repository.TodoRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@ActiveProfiles("local")
class SampleRepositoryTest {

  @Autowired
  SampleRepository sampleRepository;

  @Autowired
  TodoRepository todoRepository;

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  EntityManager entityManager;

  @Order(1)
  @Transactional
  @DisplayName("getTodos_QueryDSL을 사용하여 To-Do 목록 조회")
  @Test
  void testGetTodos() {

    // Given
    setUpTodos();

    TodoEntity todo = TodoEntity.builder()
        .title("Title Test")
        .description("Description Test")
        .completed(false)
        .build();

    // When
    List<TodoEntity> todos = sampleRepository.getTodos(todo);

    // Then
    log.debug("todos : {}", todos);
    assertFalse(todos.isEmpty());
  }

  @Order(2)
  @Transactional
  @DisplayName("getTodos_Pageable을 사용하여 To-Do 목록 조회")
  @Test
  void testGetTodosPageable() {

    // Given
    setUpTodos();

    TodoEntity todo = TodoEntity.builder()
        .title("Title Test")
        .description("Description Test")
        .completed(true)
        .build();

    // When
    Page<TodoEntity> todoPage = sampleRepository.getTodos(todo, PageRequest.of(0, 5));

    // Then
    log.debug("Content : {}", todoPage.getContent());
    log.debug("Size : {}", todoPage.getSize());
    log.debug("TotalPages : {}", todoPage.getTotalPages());
    log.debug("TotalElements : {}", todoPage.getTotalElements());
    log.debug("NextPageable : {}", todoPage.nextPageable());
    assertFalse(todoPage.isEmpty());
  }

  private void clearPersistenceContext() {
    entityManager.flush();
    entityManager.clear();
  }

  private Long insertMember(String name, String email) {
    return memberRepository.save(
        MemberEntity.builder()
            .name(name)
            .email(email)
            .build()
    ).getId();
  }

  private void insertTodo(Long memberId, String title, String description, Boolean completed) {
    todoRepository.save(
        TodoEntity.builder()
            .member(MemberEntity.builder()
                .id(memberId)
                .build())
            .title(title)
            .description(description)
            .completed(completed)
            .build()
    );
  }

  private void setUpTodos() {
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
      completed = a % 2 == 0;

      insertTodo(memberId, title, description, completed);
    }

    clearPersistenceContext();
  }
}
