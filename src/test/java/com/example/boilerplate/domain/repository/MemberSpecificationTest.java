package com.example.boilerplate.domain.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;

import com.example.boilerplate.domain.entity.MemberEntity;
import com.example.boilerplate.domain.entity.TodoEntity;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@ActiveProfiles("local")
class MemberSpecificationTest {

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  TodoRepository todoRepository;

  @Autowired
  EntityManager entityManager;

  @Order(1)
  @Transactional
  @DisplayName("Specification(likeName)을 사용하여 Member 목록을 조회")
  @Test
  void testFindAllSpecLikeName() {

    // Given
    setUpMembers();
    clearPersistenceContext();

    // When
    List<MemberEntity> memberList = memberRepository.findAll(
        MemberSpecification.likeName("test")
    );

    // Then
    log.debug("memberList : {}", memberList);
    assertFalse(memberList.isEmpty());
  }

  @Order(2)
  @Transactional
  @DisplayName("Specification(likeEmail)을 사용하여 Member 목록을 조회")
  @Test
  void testFindAllSpecLikeEmail() {

    // Given
    setUpMembers();
    clearPersistenceContext();

    // When
    List<MemberEntity> memberList = memberRepository.findAll(
        MemberSpecification.likeEmail("gmail")
    );

    // Then
    log.debug("memberList : {}", memberList);
    assertFalse(memberList.isEmpty());
  }

  @Order(3)
  @Transactional
  @DisplayName("Specification(likeName)을 사용할 때 정렬 조건(Sort)를 추가하여 Member 목록을 조회")
  @Test
  void testFindAllSpecLikeNameSort() {

    // Given
    setUpMembers();
    clearPersistenceContext();

    Sort sort = Sort.by(Sort.Order.desc("id"));

    // When
    List<MemberEntity> memberList = memberRepository.findAll(
        MemberSpecification.likeName("test"),
        sort
    );

    // Then
    log.debug("memberList : {}", memberList);
    assertFalse(memberList.isEmpty());
  }

  @Disabled
  private void clearPersistenceContext() {
    entityManager.flush();
    entityManager.clear();
  }

  @Disabled
  private void setUpMembers() {

    for (int a = 1; a <= 10; a++) {

      String name = "test" + String.format("%02d", a);
      String email = a % 2 == 0 ? name + "@naver.com" : name + "@gmail.com";

      Long memberId = memberRepository.save(
          MemberEntity.builder()
              .name(name)
              .email(email)
              .build()
      ).getId();

      for (int b = 1; b <= 5; b++) {
        todoRepository.save(createTodoEntity(memberId, b));
      }
    }
  }

  @Disabled
  private TodoEntity createTodoEntity(Long memberId, int index) {

    String title = "Title Test" + String.format("%02d", index);
    String description = "Description Test" + String.format("%02d", index);
    Boolean completed = index % 2 == 0;

    return TodoEntity.builder()
        .member(MemberEntity.builder().id(memberId).build())
        .title(title)
        .description(description)
        .completed(completed)
        .build();
  }
}
