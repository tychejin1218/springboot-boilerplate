package com.example.boilerplate.member.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.boilerplate.common.exception.ApiException;
import com.example.boilerplate.common.type.ApiStatus;
import com.example.boilerplate.domain.entity.MemberEntity;
import com.example.boilerplate.domain.repository.MemberRepository;
import com.example.boilerplate.domain.repository.TodoRepository;
import com.example.boilerplate.member.dto.MemberDto;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("local")
@SpringBootTest
class MemberQueryRepositoryTest {

  private static final String MEMBER_EMAIL_PREFIX = "tester";
  private static final String MEMBER_NAME_PREFIX = "테스트";
  private static final String NON_EXISTENT_MEMBER_NAME = "존재하지않는이름";
  private static final String NON_EXISTENT_MEMBER_EMAIL = "nonexistent@example.com";

  private static final String TODO_TITLE_PREFIX = "할 일 제목";
  private static final String TODO_DESCRIPTION_PREFIX = "할 일 내용";

  @Autowired
  private MemberQueryRepository memberQueryRepository;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private TodoRepository todoRepository;

  @Autowired
  private EntityManager entityManager;

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("getMemberList - 회원 목록 조회")
  @Nested
  class TestGetMemberList {

    @BeforeEach
    void setUp() {
      setUpMemberAndTodoList();
      clearPersistenceContext();
    }

    @Order(1)
    @DisplayName("검색 조건 없이 회원 목록 조회")
    @Transactional
    @Test
    void testGetMemberList() {

      // Given
      MemberDto.Request memberRequest = MemberDto.Request.builder().build();

      // When
      List<MemberDto.Response> memberList = memberQueryRepository.getMemberList(memberRequest);

      // Then
      assertFalse(memberList.isEmpty());
    }

    @Order(2)
    @DisplayName("이름 조건으로 회원 목록 조회")
    @Transactional
    @Test
    void testGetMemberListByName() {

      // Given
      String memberName = MEMBER_NAME_PREFIX + "_1";
      MemberDto.Request memberRequest = MemberDto.Request.of(null, memberName);

      // When
      List<MemberDto.Response> memberList = memberQueryRepository.getMemberList(memberRequest);

      // Then
      assertFalse(memberList.isEmpty());
    }

    @Order(3)
    @DisplayName("이메일 조건으로 회원 목록 조회")
    @Transactional
    @Test
    void testGetMemberListByEmail() {

      // Given
      String memberEmail = MEMBER_EMAIL_PREFIX + "_1";
      MemberDto.Request memberRequest = MemberDto.Request.of(memberEmail, null);

      // When
      List<MemberDto.Response> memberList = memberQueryRepository.getMemberList(memberRequest);

      // Then
      assertFalse(memberList.isEmpty());
    }

    @Order(4)
    @DisplayName("이름과 이메일 조건으로 회원 목록 조회")
    @Transactional
    @Test
    void testGetMemberListByNameAndEmail() {

      // Given
      String memberEmail = MEMBER_EMAIL_PREFIX + "_1";
      String memberName = MEMBER_NAME_PREFIX + "_1";
      MemberDto.Request memberRequest = MemberDto.Request.of(memberEmail, memberName);

      // When
      List<MemberDto.Response> memberList = memberQueryRepository.getMemberList(memberRequest);

      // Then
      assertFalse(memberList.isEmpty());
    }

    @Order(5)
    @DisplayName("존재하지 않는 이름으로 회원 목록 조회 시 빈 리스트 반환")
    @Transactional
    @Test
    void testGetMemberListWithNonExistentName() {

      // Given
      MemberDto.Request memberRequest = MemberDto.Request.of(null, NON_EXISTENT_MEMBER_NAME);

      // When
      List<MemberDto.Response> memberList = memberQueryRepository.getMemberList(memberRequest);

      // Then
      assertTrue(memberList.isEmpty());
    }

    @Order(6)
    @DisplayName("존재하지 않는 이메일로 회원 목록 조회 시 빈 리스트 반환")
    @Transactional
    @Test
    void testGetMemberListWithNonExistentEmail() {

      // Given
      MemberDto.Request memberRequest = MemberDto.Request.of(NON_EXISTENT_MEMBER_EMAIL, null);

      // When
      List<MemberDto.Response> memberList = memberQueryRepository.getMemberList(memberRequest);

      // Then
      assertTrue(memberList.isEmpty());
    }
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("getPagedMemberList - 페이징이 적용된 회원 목록 조회")
  @Nested
  class TestGetPagedMemberList {

    @BeforeEach
    void setUp() {
      setUpMemberAndTodoList();
      clearPersistenceContext();
    }

    @Order(1)
    @DisplayName("페이징이 적용된 회원 목록 조회")
    @Transactional
    @Rollback()
    @Test
    void testGetPagedMemberList() {

      // Given
      MemberDto.PageRequest pageRequest = MemberDto.PageRequest.of(null, null, 0, 3, null);

      // When
      Page<MemberDto.Response> pageResult = memberQueryRepository.getPagedMemberList(pageRequest);

      // Then
      assertAll(
          () -> assertEquals(3, pageResult.getContent().size()),
          () -> assertTrue(pageResult.getTotalElements() > 0),
          () -> assertTrue(pageResult.getTotalPages() > 0)
      );
    }

    @Order(2)
    @DisplayName("페이징 및 정렬 조건이 적용된 회원 목록 조회")
    @Transactional
    @Test
    void testGetPagedMemberListWithSorting() {

      // TODO : 정렬조건 테스트
      // Given
      MemberDto.PageRequest pageRequest = MemberDto.PageRequest.of(null, null, 1, 3,
          List.of("name,desc"));

      // When
      Page<MemberDto.Response> pageResult = memberQueryRepository.getPagedMemberList(pageRequest);

      // Then
      assertAll(
          () -> assertEquals(3, pageResult.getContent().size()),
          () -> assertTrue(pageResult.getTotalElements() > 0),
          () -> assertTrue(pageResult.getTotalPages() > 0)
      );
    }

    @Order(3)
    @DisplayName("이름, 이메일 조건과 페이징 및 정렬이 적용된 포함된 회원 목록 조회")
    @Transactional
    @Test
    void testGetPagedMemberListByNameAndEmailWithSorting() {

      // Given
      String memberEmail = MEMBER_EMAIL_PREFIX + "_1";
      String memberName = MEMBER_NAME_PREFIX + "_1";
      MemberDto.PageRequest pageRequest = MemberDto.PageRequest
          .of(memberEmail, memberName, 1, 3, null);

      // When
      Page<MemberDto.Response> pageResult = memberQueryRepository.getPagedMemberList(pageRequest);

      // Then
      assertAll(
          () -> assertEquals(1, pageResult.getContent().size()),
          () -> assertTrue(pageResult.getTotalElements() > 0),
          () -> assertTrue(pageResult.getTotalPages() > 0)
      );
    }

    @Order(4)
    @DisplayName("존재하지 않는 페이지 요청 시 빈 페이지 반환")
    @Transactional
    @Test
    void testGetPagedMemberListWithOutOfRangePage() {

      // Given
      MemberDto.PageRequest pageRequest = MemberDto.PageRequest.of(null, null, 999, 3, null);

      // When
      Page<MemberDto.Response> pageResult = memberQueryRepository.getPagedMemberList(pageRequest);

      // Then
      assertAll(
          () -> assertTrue(pageResult.getContent().isEmpty()),
          () -> assertEquals(999, pageResult.getNumber() + 1)
      );
    }

    @Order(5)
    @DisplayName("잘못된 정렬 필드 요청 시 예외 발생")
    @Transactional
    @Test
    void testGetPagedMemberListWithInvalidSortField() {

      // Given
      MemberDto.PageRequest pageRequest = MemberDto.PageRequest.of(null, null, 1, 3,
          List.of("invalidField,desc"));

      // When
      ApiException apiException = Assertions.assertThrows(
          ApiException.class, () -> memberQueryRepository.getPagedMemberList(pageRequest));

      // Then
      assertAll(
          () -> assertEquals(ApiStatus.METHOD_ARGUMENT_NOT_VALID.getCode(),
              apiException.getStatus().getCode()),
          () -> assertEquals(ApiStatus.METHOD_ARGUMENT_NOT_VALID.getMessage(),
              apiException.getStatus().getMessage())
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
    for (int i = 1; i <= 10; i++) {
      String memberEmail = MEMBER_EMAIL_PREFIX + "_" + i + "@example.com";
      String memberName = MEMBER_NAME_PREFIX + "_" + i;
      memberRepository.save(MemberEntity.builder()
          .email(memberEmail)
          .name(memberName)
          .build());
    }
  }
}
