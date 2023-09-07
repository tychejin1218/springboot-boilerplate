package com.example.boilerplate.sample.domain.repository;


import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.boilerplate.sample.domain.entity.Member;
import com.example.boilerplate.sample.domain.entity.Todo;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest
@ActiveProfiles("local")
@Disabled
class TodoRepositoryN1Test {

  @Autowired
  private TodoRepository todoRepository;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  EntityManager entityManager;

  @Transactional
  @DisplayName("N + 1 문제 확인")
  @Test
  void testFindAll() {

    // Given
    setUpMembers();

    // When
    List<Todo> todos = todoRepository.findAll();
    for (Todo todo : todos) {
      // N + 1 문제 : 연관 관계가 설정된 엔티티를 조회할 경우에 조회된 데이터 갯수(N) 만큼 연관 관계의 SELECT 쿼리가 추가로 발생
      // 해결 방법
      // 1. fetch join -> inner join
      // 2. @EntityGraph -> left outer join
      log.info("Member의 name을 조회 : {}", todo.getMember().getName());
    }

    // Then
    assertTrue(!todos.isEmpty());
  }

  /**
   * Member, To-Do 목록을 설정
   */
  @Disabled
  void setUpMembers() {

    for (int a = 0; a < 10; a++) {

      Member member = Member.builder()
          .name("test" + a)
          .email("test" + a + "@gmail.com")
          .build();
      memberRepository.save(member);

      todoRepository.save(
          Todo.builder()
              .member(member)
              .title("title" + a)
              .description("description" + a)
              .completed(false)
              .build()
      );
    }

    entityManager.flush();
    entityManager.clear();
  }
}
