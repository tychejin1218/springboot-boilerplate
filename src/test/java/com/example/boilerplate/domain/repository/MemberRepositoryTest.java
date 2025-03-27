package com.example.boilerplate.domain.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
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
@ActiveProfiles("local")
@SpringBootTest
class MemberRepositoryTest {

  private static final String TEST_NAME = "test01";
  private static final String TEST_EMAIL = "test01@gmail.com";

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  TodoRepository todoRepository;

  @Autowired
  EntityManager entityManager;

  @Order(1)
  @DisplayName("모든 회원 조회 시, 모든 회원 반환")
  @Transactional
  @Test
  void testFindAllMembers() {

    // Given
    setUpMemberList();
    clearPersistenceContext();

    // When
    List<MemberEntity> memberList = memberRepository.findAll();

    // Then
    log.debug("memberList : {}", memberList);
    assertFalse(memberList.isEmpty());
  }

  @Order(2)
  @DisplayName("회원 ID로 조회 시, 해당 회원 반환")
  @Transactional
  @Test
  void testFindMemberById() {

    // Given
    Long memberId = saveMemberAndReturnId(TEST_NAME, TEST_EMAIL);
    clearPersistenceContext();
    log.debug("memberId : {}", memberId);

    // When
    Optional<MemberEntity> optMember = memberRepository.findById(memberId);

    // Then
    assertTrue(optMember.isPresent());
    MemberEntity memberEntity = optMember.get();
    assertEquals(TEST_NAME, memberEntity.getName());
    assertEquals(TEST_EMAIL, memberEntity.getEmail());
  }

  @Order(3)
  @DisplayName("회원 저장 요청 시, 저장된 회원 반환")
  @Transactional
  @Test
  void testSaveNewMember() throws Exception {

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
  @DisplayName("회원 수정 요청 시, 수정된 회원 반환")
  @Transactional
  @Test
  void testUpdateExistingMember() throws Exception {

    // Given
    Long memberId = saveMemberAndReturnId(TEST_NAME, TEST_EMAIL);
    clearPersistenceContext();

    MemberEntity requestMember = MemberEntity.builder()
        .id(memberId)
        .name("test02")
        .email("test02@gmail.com")
        .build();

    // When
    MemberEntity updatedMember = memberRepository.save(requestMember);
    clearPersistenceContext();

    // Then
    MemberEntity savedMember = memberRepository.findById(updatedMember.getId())
        .orElseThrow(() -> new Exception("회원 수정 실패"));

    assertAll(
        () -> assertEquals(requestMember.getName(), savedMember.getName()),
        () -> assertEquals(requestMember.getEmail(), savedMember.getEmail())
    );
  }

  @Order(5)
  @DisplayName("회원 삭제 요청 시, 해당 회원 제거")
  @Transactional
  @Test
  void testDeleteMemberById() {

    // Given
    Long memberId = saveMemberAndReturnId(TEST_NAME, TEST_EMAIL);
    clearPersistenceContext();

    // When
    memberRepository.deleteById(memberId);

    // Then
    assertTrue(memberRepository.findById(memberId).isEmpty());
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
  private Long saveMemberAndReturnId(String name, String email) {
    return saveMemberAndEntity(name, email).getId();
  }

  @Disabled
  private void setUpMemberList() {
    for (int i = 1; i <= 5; i++) {
      String name = "test" + i;
      String email = "test" + i + "@example.com";
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
