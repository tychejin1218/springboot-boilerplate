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
@ActiveProfiles("local")
@SpringBootTest
class MemberSpecificationTest {

  private static final String TEST_NAME_PREFIX = "test";

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  TodoRepository todoRepository;

  @Autowired
  EntityManager entityManager;

  @Order(1)
  @DisplayName("Specification(likeName)을 사용하여 회원 목록을 조회")
  @Transactional
  @Test
  void testFindMembersByNameUsingSpecification() {

    // Given
    setUpMemberList();
    clearPersistenceContext();

    // When
    List<MemberEntity> memberList = memberRepository.findAll(
        MemberSpecification.likeName(TEST_NAME_PREFIX) // 상수 사용
    );

    // Then
    log.debug("memberList : {}", memberList);
    assertFalse(memberList.isEmpty());
  }

  @Order(2)
  @DisplayName("Specification(likeEmail)을 사용하여 회원 목록을 조회")
  @Transactional
  @Test
  void testFindMembersByEmailUsingSpecification() {

    // Given
    setUpMemberList();
    clearPersistenceContext();

    // When
    List<MemberEntity> memberList = memberRepository.findAll(
        MemberSpecification.likeEmail("gmail") // 그대로 유지 (중복 없음)
    );

    // Then
    log.debug("memberList : {}", memberList);
    assertFalse(memberList.isEmpty());
  }

  @Order(3)
  @DisplayName("Specification(likeName)을 사용할 때 정렬 조건(Sort)를 추가하여 회원 목록을 조회")
  @Transactional
  @Test
  void testFindMembersByNameWithSortingUsingSpecification() {

    // Given
    setUpMemberList();
    clearPersistenceContext();

    Sort sort = Sort.by(Sort.Order.desc("id"));

    // When
    List<MemberEntity> memberList = memberRepository.findAll(
        MemberSpecification.likeName(TEST_NAME_PREFIX), // 상수 사용
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
  private MemberEntity saveMemberAndEntity(String name, String email) {
    return memberRepository.save(
        MemberEntity.builder()
            .name(name)
            .email(email)
            .build());
  }

  @Disabled
  private void setUpMemberList() {
    for (int i = 1; i <= 5; i++) {
      String name = TEST_NAME_PREFIX + i; // 상수 사용
      String email = TEST_NAME_PREFIX + i + "@example.com"; // 상수 사용
      MemberEntity memberEntity = memberRepository.save(saveMemberAndEntity(name, email));
      for (int j = 1; j <= 3; j++) {
        todoRepository.save(createTodoEntity(memberEntity.getId(), j));
      }
    }
  }

  @Disabled
  private TodoEntity createTodoEntity(Long memberId, int index) {
    String title = "할 일 " + index;
    String description = "할 일 설명 " + index;
    boolean completed = index % 2 == 0;
    return TodoEntity.builder()
        .memberId(memberId)
        .title(title)
        .description(description)
        .completed(completed)
        .build();
  }
}
