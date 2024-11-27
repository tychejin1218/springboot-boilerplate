package com.example.boilerplate.domain.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.boilerplate.domain.entity.MemberEntity;
import com.example.boilerplate.domain.entity.TodoEntity;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@ActiveProfiles("local")
class MemberRepositoryTest {

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  TodoRepository todoRepository;

  @Autowired
  EntityManager entityManager;

  @Order(1)
  @Transactional
  @DisplayName("findAll_회원 목록 조회")
  @Test
  void testFindAll() {

    // Given
    setUpMembers();
    clearPersistenceContext();

    // When
    List<MemberEntity> memberList = memberRepository.findAll();

    // Then
    log.debug("memberList : {}", memberList);
    assertFalse(memberList.isEmpty());
  }

  @Order(2)
  @Transactional
  @DisplayName("findById_회원 조회")
  @Test
  void testFindById() {

    // Given
    Long memberId = getMemberIdAfterInsertTodos();
    clearPersistenceContext();
    log.debug("memberId : {}", memberId);

    // When
    Optional<MemberEntity> opMember = memberRepository.findById(memberId);

    // Then
    log.debug("opMember : {}", opMember);
    assertTrue(opMember.isPresent());
  }

  @Order(3)
  @Transactional
  @DisplayName("save_회원 저장")
  @Test
  void testInsert() throws Exception {

    // Given
    MemberEntity requestMember = MemberEntity.builder()
        .name("test01")
        .email("test01@gmail.com")
        .build();

    // When
    memberRepository.save(requestMember);
    clearPersistenceContext();

    // Then
    Optional<MemberEntity> opMember = memberRepository.findById(requestMember.getId());
    if (opMember.isPresent()) {
      MemberEntity insertMember = opMember.get();
      assertEquals(requestMember.getName(), insertMember.getName());
      assertEquals(requestMember.getEmail(), insertMember.getEmail());
    } else {
      log.debug("requestMember : {}, opMember : {}", requestMember, opMember);
      throw new Exception();
    }
  }

  @Order(4)
  @Transactional
  @DisplayName("save_회원 수정")
  @Test
  void testUpdate() throws Exception {

    // Given
    MemberEntity insertMember = getMemberAfterInsertMember();
    clearPersistenceContext();

    MemberEntity requestMember = MemberEntity.builder()
        .id(insertMember.getId())
        .name("test02")
        .email("test02@gmail.com")
        .build();

    // When
    memberRepository.save(requestMember);
    clearPersistenceContext();

    // Then
    Optional<MemberEntity> opMember = memberRepository.findById(requestMember.getId());
    if (opMember.isPresent()) {
      MemberEntity updateMember = opMember.get();
      assertEquals(requestMember.getName(), updateMember.getName());
      assertEquals(requestMember.getEmail(), updateMember.getEmail());
    } else {
      log.debug("requestMember : {}, opMember : {}", requestMember, opMember);
      throw new Exception();
    }
  }

  @Order(5)
  @Transactional
  @DisplayName("deleteById_회원 삭제")
  @Test
  void testDelete() {

    // Given
    MemberEntity insertMember = getMemberAfterInsertMember();
    clearPersistenceContext();

    // When
    memberRepository.deleteById(insertMember.getId());

    // Then
    assertTrue(memberRepository.findById(insertMember.getId()).isEmpty());
  }

  @Disabled
  private void clearPersistenceContext() {
    entityManager.flush();
    entityManager.clear();
  }

  @Disabled
  private MemberEntity getMemberAfterInsertMember() {
    return memberRepository.save(
        MemberEntity.builder()
            .name("test")
            .email("test@gmail.com")
            .build());
  }

  @Disabled
  private Long getMemberIdAfterInsertTodos() {

    Long memberId = memberRepository.save(
        MemberEntity.builder()
            .name("test")
            .email("test@gmail.com")
            .build()
    ).getId();

    for (int a = 1; a <= 10; a++) {
      todoRepository.save(createTodoEntity(memberId, a));
    }

    return memberId;
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
