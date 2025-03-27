package com.example.boilerplate.member.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.boilerplate.common.exception.ApiException;
import com.example.boilerplate.common.type.ApiStatus;
import com.example.boilerplate.domain.entity.MemberEntity;
import com.example.boilerplate.domain.repository.MemberRepository;
import com.example.boilerplate.member.dto.MemberDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;
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
@ActiveProfiles("local")
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MemberServiceTest {

  private static final String MEMBER_EMAIL_PREFIX = "tester";
  private static final String MEMBER_NAME_PREFIX = "테스터";
  private static final String MEMBER_PASSWORD = "password1!";

  private static final String UPDATED_MEMBER_EMAIL = "tester2@example.com";
  private static final String UPDATED_MEMBER_NAME = "테스터2";

  @Autowired
  private MemberService memberService;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private ObjectMapper objectMapper;

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("getMemberList - 회원 목록 조회")
  @Nested
  class TestGetMemberList {

    @BeforeEach
    void setUp() {
      setUpMemberList();
      clearPersistenceContext();
    }

    @Order(1)
    @DisplayName("회원 목록 조회 성공")
    @Transactional
    @Test
    void testGetMemberListSuccess() {

      // Given
      String memberEmail = MEMBER_EMAIL_PREFIX + "1";
      String memberName = MEMBER_NAME_PREFIX + "1";
      MemberDto.Request memberRequest = MemberDto.Request.of(memberEmail, memberName);

      // When
      List<MemberDto.Response> memberList = memberService.getMemberList(memberRequest);

      // Then
      assertFalse(memberList.isEmpty());
    }
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("getPagedMemberList - 페이징이 적용된 회원 목록 조회")
  @Nested
  class TestGetPagedTodoList {

    @BeforeEach
    void setUp() {
      setUpMemberList();
      clearPersistenceContext();
    }

    @Order(1)
    @DisplayName("페이징이 적용된 할 일 목록 조회")
    @Transactional
    @Test
    void testGetPagedMemberListSuccess() {

      // Given
      MemberDto.PageRequest pageRequest = MemberDto.PageRequest.of(null, null, 1, 3, null);

      // When
      Page<MemberDto.Response> pageResult = memberService.getPagedMemberList(pageRequest);

      // Then
      assertAll(
          () -> assertEquals(3, pageResult.getContent().size()),
          () -> assertTrue(pageResult.getTotalElements() > 0),
          () -> assertTrue(pageResult.getTotalPages() > 0)
      );
    }

    @Order(2)
    @DisplayName("페이징 및 정렬 조건이 포함된 회원 목록 조회")
    @Transactional
    @Test
    void testGetPagedMemberListWithSorting() {

      // Given
      MemberDto.PageRequest pageRequest = MemberDto.PageRequest.of(null, null, 1, 3,
          //List.of("email,desc", "name,desc"));
          List.of("email,desc"));
      //MemberDto.PageRequest pageRequest = MemberDto.PageRequest.of(null, null, 1, 3, null);

      // When
      Page<MemberDto.Response> pageResult = memberService.getPagedMemberList(pageRequest);

      // Then
      assertAll(
          () -> assertEquals(3, pageResult.getContent().size()),
          () -> assertTrue(pageResult.getTotalElements() > 0),
          () -> assertTrue(pageResult.getTotalPages() > 0),
          () -> assertTrue(pageResult.getContent().get(0).getEmail()
              .compareTo(pageResult.getContent().get(1).getEmail()) >= 0)
      );
    }

    @Order(3)
    @DisplayName("잘못된 정렬 필드 요청 시 예외 발생")
    @Transactional
    @Test
    void testGetPagedMemberListWithInvalidSortField() {

      // Given
      MemberDto.PageRequest pageRequest = MemberDto.PageRequest.of(null, null, 1, 3,
          List.of("invalidField,desc"));

      // When
      ApiException apiException = assertThrows(
          ApiException.class, () -> memberService.getPagedMemberList(pageRequest));

      // Then
      assertAll(
          () -> assertEquals(ApiStatus.METHOD_ARGUMENT_NOT_VALID.getCode(),
              apiException.getStatus().getCode()),
          () -> assertEquals(ApiStatus.METHOD_ARGUMENT_NOT_VALID.getMessage(),
              apiException.getStatus().getMessage())
      );
    }
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("getMember - 특정 회원 조회")
  @Nested
  class TestGetMember {

    @Order(1)
    @DisplayName("특정 회원 조회 성공")
    @Transactional
    @Test
    void testGetMemberSuccess() {

      // Given
      String uniqueId = UUID.randomUUID().toString().substring(0, 8);
      String memberEmail = MEMBER_EMAIL_PREFIX + uniqueId;
      String memberName = MEMBER_NAME_PREFIX + uniqueId;
      Long memberId = saveMemberAndReturnId(memberEmail, memberName, MEMBER_PASSWORD);
      clearPersistenceContext();

      // When
      MemberDto.Response memberResponse = memberService.getMember(memberId);

      // Then
      assertAll(
          () -> assertEquals(MEMBER_NAME_PREFIX, memberResponse.getName()),
          () -> assertEquals(MEMBER_EMAIL_PREFIX, memberResponse.getEmail())
      );
    }

    @Order(2)
    @DisplayName("존재하지 않은 회원 아이디로 조회 실패")
    @Transactional
    @Test
    void testGetMemberNotFoundId() {

      // Given
      Long notFoundId = 0L;

      // When
      ApiException apiException = assertThrows(
          ApiException.class, () -> memberService.getMember(notFoundId));

      // Then
      assertAll(
          () -> assertEquals(ApiStatus.MEMBER_NOT_FOUND.getCode(),
              apiException.getStatus().getCode()),
          () -> assertEquals(ApiStatus.MEMBER_NOT_FOUND.getMessage(),
              apiException.getStatus().getMessage())
      );
    }
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("insertMember - 회원 추가")
  @Nested
  class TestInsertMember {

    @Order(1)
    @DisplayName("회원 추가 성공")
    @Transactional
    @Test
    void testInsertMemberSuccess() {

      // Given
      String uniqueId = UUID.randomUUID().toString().substring(0, 8);
      String memberEmail = MEMBER_EMAIL_PREFIX + uniqueId;
      String memberName = MEMBER_NAME_PREFIX + uniqueId;
      MemberDto.InsertRequest insertRequest = MemberDto.InsertRequest.of(
          memberEmail, memberName, MEMBER_PASSWORD);

      // When
      MemberDto.Response memberResponse = memberService.insertMember(insertRequest);
      clearPersistenceContext();

      // Then
      MemberDto.Response savedResponse = memberService.getMember(memberResponse.getId());
      assertAll(
          () -> assertEquals(insertRequest.getEmail(), savedResponse.getEmail()),
          () -> assertEquals(insertRequest.getName(), savedResponse.getName())
      );
    }

    @Order(2)
    @DisplayName("이메일 중복으로 회원 추가 실패")
    @Transactional
    @Test
    void testInsertMemberEmailDuplicate() {

      // Given
      String uniqueId = UUID.randomUUID().toString().substring(0, 8);
      String memberEmail = MEMBER_EMAIL_PREFIX + uniqueId;
      String memberName = MEMBER_NAME_PREFIX + uniqueId;
      saveMemberAndReturnId(memberEmail, memberName, MEMBER_PASSWORD);

      MemberDto.InsertRequest duplicateRequest = MemberDto.InsertRequest.of(
          memberEmail, memberName, MEMBER_PASSWORD);

      // When
      ApiException apiException = assertThrows(ApiException.class, () ->
          memberService.insertMember(duplicateRequest));

      // Then
      assertAll(
          () -> assertEquals(ApiStatus.ALREADY_EXISTS_EMAIL.getCode(),
              apiException.getStatus().getCode()),
          () -> assertEquals(ApiStatus.ALREADY_EXISTS_EMAIL.getMessage(),
              apiException.getStatus().getMessage())
      );
    }
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("updateMember - 회원 정보 수정")
  @Nested
  class TestUpdateMember {

    @Order(1)
    @DisplayName("회원 정보 수정 성공")
    @Transactional
    @Test
    void testUpdateMemberSuccess() {

      // Given
      String uniqueId = UUID.randomUUID().toString().substring(0, 8);
      String memberEmail = MEMBER_EMAIL_PREFIX + uniqueId;
      String memberName = MEMBER_NAME_PREFIX + uniqueId;
      Long memberId = saveMemberAndReturnId(memberEmail, memberName, MEMBER_PASSWORD);
      clearPersistenceContext();

      MemberDto.UpdateRequest updateRequest = MemberDto.UpdateRequest.of(
          memberId, UPDATED_MEMBER_EMAIL, UPDATED_MEMBER_NAME);

      // When
      MemberDto.Response updatedResponse = memberService.updateMember(updateRequest);
      clearPersistenceContext();

      // Then
      assertAll(
          () -> assertEquals(updateRequest.getName(), updatedResponse.getName()),
          () -> assertEquals(updateRequest.getEmail(), updatedResponse.getEmail())
      );
    }

    @Order(2)
    @DisplayName("존재하지 않는 회원 아이디로 수정 시 실패")
    @Transactional
    @Test
    void testUpdateMemberNotFound() {

      // Given
      MemberDto.UpdateRequest updateRequest = MemberDto.UpdateRequest.of(
          0L, MEMBER_EMAIL_PREFIX + "수정", MEMBER_NAME_PREFIX + "수정");

      // When
      ApiException exception = assertThrows(ApiException.class, () ->
          memberService.updateMember(updateRequest));

      // Then
      assertEquals(ApiStatus.MEMBER_NOT_FOUND.getCode(), exception.getStatus().getCode());
    }
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("deleteMember - 회원 삭제")
  @Nested
  class TestDeleteMember {

    @Order(1)
    @DisplayName("회원 삭제 성공")
    @Transactional
    @Test
    void testDeleteMemberSuccess() {

      // Given
      Long memberId = saveMemberAndReturnId(MEMBER_EMAIL_PREFIX, MEMBER_NAME_PREFIX,
          MEMBER_PASSWORD);
      clearPersistenceContext();

      // When
      memberService.deleteMember(memberId);

      // Then
      assertTrue(memberRepository.findById(memberId).isEmpty());
    }

    @Order(2)
    @DisplayName("존재하지 않은 회원 아이디로 삭제 시 실패")
    @Transactional
    @Test
    void testDeleteMemberNotFound() {

      // Given
      Long notFoundId = 0L;

      // When
      ApiException exception = assertThrows(ApiException.class, () ->
          memberService.deleteMember(notFoundId));

      // Then
      assertEquals(ApiStatus.MEMBER_NOT_FOUND.getCode(), exception.getStatus().getCode());
    }
  }

  @Disabled
  private void clearPersistenceContext() {
    entityManager.flush();
    entityManager.clear();
  }

  @Disabled
  private void setUpMemberList() {
    for (int i = 1; i <= 5; i++) {
      String memberEmail = MEMBER_EMAIL_PREFIX + "_" + i + "@example.com";
      String memberName = MEMBER_NAME_PREFIX + "_" + i;
      memberRepository.save(
          MemberEntity.builder()
              .email(memberEmail)
              .name(memberName)
              .password(MEMBER_PASSWORD)
              .build());
    }
  }

  @Disabled
  private Long saveMemberAndReturnId(String email, String name, String password) {
    return memberRepository.save(
        MemberEntity.builder()
            .email(email)
            .name(name)
            .password(password)
            .build()).getId();
  }
}
