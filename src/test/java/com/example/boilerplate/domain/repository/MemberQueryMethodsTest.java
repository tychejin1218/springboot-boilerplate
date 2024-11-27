package com.example.boilerplate.domain.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;

import com.example.boilerplate.domain.entity.MemberEntity;
import com.example.boilerplate.domain.entity.TodoEntity;
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
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@ActiveProfiles("local")
class MemberQueryMethodsTest {

  private static final String NAME_TEST_STR = "test";
  private static final String EMAIL_SUF_TEST_STR = "gmail.com";

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  TodoRepository todoRepository;

  @Autowired
  EntityManager entityManager;

  @Order(1)
  @Transactional
  @DisplayName("Query Method을 사용하여 Member 목록을 조회")
  @Test
  void testFindAllByNameContainingIgnoreCaseAndEmailContainingIgnoreCaseOrderByIdDesc() {

    // Given
    setUpMembers();
    clearPersistenceContext();

    // When
    List<MemberEntity> memberList = memberRepository
        .findAllByNameContainingIgnoreCaseAndEmailContainingIgnoreCaseOrderByIdDesc(
            NAME_TEST_STR,
            EMAIL_SUF_TEST_STR
        );

    // Then
    log.debug("memberList : {}", memberList);
    assertFalse(memberList.isEmpty());
  }

  @Order(2)
  @Transactional
  @DisplayName("Query Method을 사용할 때 정렬 조건(Sort)를 추가하여 Member 목록을 조회")
  @Test
  void testFindAllByNameContainingIgnoreCaseAndEmailContainingIgnoreCase() {

    // Given
    setUpMembers();
    clearPersistenceContext();

    Sort sort = Sort.by(Sort.Order.desc("id"));

    // When
    List<MemberEntity> memberList = memberRepository
        .findAllByNameContainingIgnoreCaseAndEmailContainingIgnoreCase(
            NAME_TEST_STR,
            EMAIL_SUF_TEST_STR,
            sort
        );

    // Then
    log.debug("memberList : {}", memberList);
    assertFalse(memberList.isEmpty());
  }

  @Order(3)
  @Transactional
  @DisplayName("Query Method을 사용할 때 페이징(Pageable)을 추가하여 Member 목록을 조회")
  @Test
  void testFindAllByNameContainsAndEmailContainsOrderOrderByIdDescPage() {

    // Given
    setUpMembers();
    clearPersistenceContext();

    // When
    Page<MemberEntity> memberPage = memberRepository.findAllByNameContainsAndEmailContainsOrderByIdDesc(
        NAME_TEST_STR,
        EMAIL_SUF_TEST_STR,
        PageRequest.of(0, 5)
    );

    // Then
    log.debug("Content:[{}]", memberPage.getContent());
    log.debug("Size:[{}]", memberPage.getSize());
    log.debug("TotalPages:[{}]", memberPage.getTotalPages());
    log.debug("TotalElements:[{}]", memberPage.getTotalElements());
    log.debug("NextPageable:[{}]", memberPage.nextPageable());
    assertFalse(memberPage.getContent().isEmpty());
  }

  private void clearPersistenceContext() {
    entityManager.flush();
    entityManager.clear();
  }

  private void setUpMembers() {

    for (int a = 1; a <= 10; a++) {

      String name = NAME_TEST_STR + String.format("%02d", a);
      String email = a % 2 == 0 ? name + "@naver.com" : name + "@" + EMAIL_SUF_TEST_STR;

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
