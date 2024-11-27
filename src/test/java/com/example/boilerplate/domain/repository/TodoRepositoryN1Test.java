package com.example.boilerplate.domain.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.boilerplate.domain.entity.MemberEntity;
import com.example.boilerplate.domain.entity.TodoEntity;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@ActiveProfiles("local")
class TodoRepositoryN1Test {

  @Autowired
  TodoRepository todoRepository;

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  EntityManager entityManager;

  @Transactional
  @DisplayName("N + 1 문제 확인")
  @Test
  void testFindAll() {

    // Given
    setUpMembers();

    // When
    List<TodoEntity> todos = todoRepository.findAll();
    for (TodoEntity todo : todos) {
      // N + 1 문제 : 연관 관계가 설정된 엔티티를 조회할 경우에 조회된 데이터 갯수(N) 만큼 연관 관계의 SELECT 쿼리가 추가로 발생
      // 해결 방법
      // 1. fetch join -> inner join
      // 2. @EntityGraph -> left outer join
      log.debug("Member의 name을 조회 : {}", todo.getMember().getName());
    }

    // Then
    assertTrue(!todos.isEmpty());
  }

  @Disabled
  void setUpMembers() {

    for (int a = 0; a < 10; a++) {

      MemberEntity member = MemberEntity.builder()
          .name("test" + a)
          .email("test" + a + "@gmail.com")
          .build();
      memberRepository.save(member);

      todoRepository.save(
          TodoEntity.builder()
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
