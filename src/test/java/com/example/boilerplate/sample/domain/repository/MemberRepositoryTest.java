package com.example.boilerplate.sample.domain.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.boilerplate.sample.domain.entity.Member;
import com.example.boilerplate.sample.domain.entity.Todo;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings({
    "PMD.AvoidDuplicateLiterals"
})
@Slf4j
@SpringBootTest
@ActiveProfiles("local")
@Disabled
class MemberRepositoryTest {

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private TodoRepository todoRepository;

  @Autowired
  private EntityManager entityManager;

  @Order(1)
  @Transactional
  @DisplayName("findAll_Member 목록 조회")
  @Test
  void testFindAll() {

    // Given
    setUpMembers();
    entityManager.flush();
    entityManager.clear();

    // When
    List<Member> members = memberRepository.findAll();

    // Then
    log.debug("members:[{}]", members);
    assertFalse(members.isEmpty());
  }

  @Order(2)
  @Transactional
  @DisplayName("findById_Member 상세 조회")
  @Test
  void testFindById() {

    // Given
    Long memberId = getMemberIdAfterInsertTodos();
    entityManager.flush();
    entityManager.clear();
    log.debug("memberId:[{}]", memberId);

    // When
    Optional<Member> opMember = memberRepository.findById(memberId);

    // Then
    log.debug("opMember:[{}]", opMember);
    assertTrue(opMember.isPresent());
  }

  @Order(3)
  @Transactional
  @DisplayName("save_Member 저장")
  @Test
  void testInsert() throws Exception {

    // Given
    Member requestMember = Member.builder()
        .name("test01")
        .email("test01@gmail.com")
        .build();

    // When
    memberRepository.save(requestMember);
    entityManager.flush();
    entityManager.clear();

    // Then
    Optional<Member> opMember = memberRepository.findById(requestMember.getId());
    if (opMember.isPresent()) {
      Member insertMember = opMember.get();
      assertEquals(requestMember.getName(), insertMember.getName());
      assertEquals(requestMember.getEmail(), insertMember.getEmail());
    } else {
      log.debug("requestMember:[{}], opMember:[{}]", requestMember, opMember);
      throw new Exception();
    }
  }

  @Order(4)
  @Transactional
  @DisplayName("save_Member 수정")
  @Test
  void testUpdate() throws Exception {

    // Given
    Member insertMember = getMemberAfterInsertMember();
    entityManager.flush();
    entityManager.clear();

    Member requestMember = Member.builder()
        .id(insertMember.getId())
        .name("test02")
        .email("test02@gmail.com")
        .build();

    // When
    memberRepository.save(requestMember);
    entityManager.flush();
    entityManager.clear();

    // Then
    Optional<Member> opMember = memberRepository.findById(requestMember.getId());
    if (opMember.isPresent()) {
      Member updateMember = opMember.get();
      assertEquals(requestMember.getName(), updateMember.getName());
      assertEquals(requestMember.getEmail(), updateMember.getEmail());
    } else {
      log.debug(" requestMember:[{}], opMember:[{}]", requestMember, opMember);
      throw new Exception();
    }
  }

  @Order(5)
  @Transactional
  @DisplayName("deleteById_Member 삭제")
  @Test
  void testDelete() {

    // Given
    Member insertMember = getMemberAfterInsertMember();
    entityManager.flush();
    entityManager.clear();

    // When
    memberRepository.deleteById(insertMember.getId());

    // Then
    assertTrue(memberRepository.findById(insertMember.getId()).isEmpty());
  }

  /**
   * Member 한개를 저장한 후 Member를 반환
   */
  @Disabled
  Member getMemberAfterInsertMember() {
    return memberRepository.save(
        Member.builder()
            .name("test")
            .email("test@gmail.com")
            .build());
  }

  /**
   * Member 1개, To-Do 10개를 저장한 후 Member.id를 반환
   */
  @Disabled
  Long getMemberIdAfterInsertTodos() {

    Long memberId = memberRepository.save(
        Member.builder()
            .name("test")
            .email("test@gmail.com")
            .build()
    ).getId();

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

    return memberId;
  }

  @Disabled
  void setUpMembers() {

    Long memberId;
    String name;
    String email;

    String title;
    String description;
    Boolean completed;

    for (int a = 1; a <= 10; a++) {

      name = "test" + String.format("%02d", a);
      if (a % 2 == 0) {
        email = name + "@naver.com";
      } else {
        email = name + "@gmail.com";
      }

      memberId = memberRepository.save(
          Member.builder()
              .name(name)
              .email(email)
              .build()
      ).getId();

      for (int b = 1; b <= 5; b++) {
        title = "Title Test" + String.format("%02d", b);
        description = "Description Test" + String.format("%02d", b);
        if (b % 2 == 0) {
          completed = true;
        } else {
          completed = false;
        }

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
    }
  }
}
