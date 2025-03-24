package com.example.boilerplate.member.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.boilerplate.domain.entity.MemberEntity;
import com.example.boilerplate.domain.entity.TodoEntity;
import com.example.boilerplate.member.dto.MemberDto;
import com.example.boilerplate.member.dto.MemberDto.Response;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("local")
@SpringBootTest
class MemberQueryRepositoryTest {

  private static final String MEMBER_NAME = "사용자 이름";
  private static final String MEMBER_EMAIL = "사용자 이메일";
  private static final String TODO_TITLE = "할 일 제목";
  private static final String TODO_DESCRIPTION = "할 일 내용";

  @Autowired
  private MemberQueryRepository memberQueryRepository;

  @Autowired
  private EntityManager entityManager;

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("getMemberList - 회원 및 관련 Todo 목록 조회 테스트")
  @Nested
  class TestGetMemberList {

    @Order(1)
    @DisplayName("조건 없이 회원 목록 조회")
    @Transactional
    @Test
    void testGetMemberList() {

      // Given
      setUpMemberAndTodoList();
      clearPersistenceContext();

      // When
      MemberDto.Request memberRequest = MemberDto.Request.builder().build();
      List<Response> memberList = memberQueryRepository.getMemberList(memberRequest);

      // Then
      assertFalse(memberList.isEmpty());
    }

    @Order(2)
    @DisplayName("이름 조건으로 조회")
    @Transactional
    @Test
    void testGetMemberListByName() {

      // Given
      setUpMemberAndTodoList();
      clearPersistenceContext();

      // When
      MemberDto.Request memberRequest = MemberDto.Request.builder()
          .name(MEMBER_NAME + " 2").build();
      List<MemberDto.Response> memberList = memberQueryRepository.getMemberList(memberRequest);

      // Then
      assertAll(
          () -> assertFalse(memberList.isEmpty()),
          () -> assertEquals(MEMBER_NAME + " 2", memberList.get(0).getName())
      );
    }

    @Order(3)
    @DisplayName("이메일 조건으로 조회")
    @Transactional
    @Test
    void testGetMemberListByEmail() {

      // Given
      setUpMemberAndTodoList();
      clearPersistenceContext();

      // When
      MemberDto.Request memberRequest = MemberDto.Request.builder()
          .email(MEMBER_EMAIL + " 3").build();
      List<MemberDto.Response> memberList = memberQueryRepository.getMemberList(memberRequest);

      // Then
      assertAll(
          () -> assertFalse(memberList.isEmpty()),
          () -> assertEquals(MEMBER_EMAIL + " 3", memberList.get(0).getEmail())
      );
    }

    @Order(4)
    @DisplayName("이름과 이메일 조건으로 조회 + Todo 검증")
    @Transactional
    @Test
    void testGetMemberListByNameAndEmail() {

      // Given
      setUpMemberAndTodoList();
      clearPersistenceContext();

      // When
      MemberDto.Request memberRequest = MemberDto.Request.builder()
          .name(MEMBER_NAME + " 4").email(MEMBER_EMAIL + " 4").build();
      List<MemberDto.Response> memberList = memberQueryRepository.getMemberList(memberRequest);

      // Then
      assertAll(
          () -> assertFalse(memberList.isEmpty(), "회원 목록은 비어 있으면 안됩니다."),
          () -> assertEquals(MEMBER_NAME + " 4", memberList.get(0).getName()),
          () -> assertEquals(MEMBER_EMAIL + " 4", memberList.get(0).getEmail()),
          () -> assertNotNull(memberList.get(0).getTodos())
      );
    }
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("getPagedMemberList - 회원 목록 페이징 조회 테스트")
  @Nested
  class TestGetPagedMemberList {

    @BeforeEach
    void setUp() {
      setUpMemberAndTodoList();
      clearPersistenceContext();
    }

    @Test
    @Order(1)
    @DisplayName("페이징 조건이 포함된 회원 목록 조회")
    @Transactional
    void testGetPagedMemberList() {

      // Given
      MemberDto.PageRequest pageRequest = MemberDto.PageRequest.builder()
          .page(1)
          .size(3)
          .build();

      // When
      Page<Response> pageResult = memberQueryRepository.getPagedMemberList(pageRequest);

      // Then
      assertAll(
          () -> assertEquals(3, pageResult.getContent().size()),
          () -> assertTrue(pageResult.getTotalElements() > 0),
          () -> assertTrue(pageResult.getTotalPages() > 0)
      );
    }
  }

  @Disabled
  private void clearPersistenceContext() {
    entityManager.flush();
    entityManager.clear();
  }

  @Disabled
  private void setUpMemberAndTodoList() {
    for (int i = 1; i <= 5; i++) {
      String name = MEMBER_NAME + " " + i;
      String email = MEMBER_EMAIL + " " + i;

      // 회원 생성
      MemberEntity member = MemberEntity.builder()
          .name(name)
          .email(email)
          .build();
      entityManager.persist(member);

      // 회원에 연관된 Todo 생성
      for (int j = 1; j <= 5; j++) {
        String title = TODO_TITLE + " " + j;
        String description = TODO_DESCRIPTION + " " + j;
        boolean completed = j % 2 == 0; // 짝수일 경우 완료 상태

        entityManager.persist(
            TodoEntity.builder()
                .title(title)
                .description(description)
                .completed(completed)
                .member(member) // 연결된 회원 설정
                .build()
        );
      }
    }
  }
}
