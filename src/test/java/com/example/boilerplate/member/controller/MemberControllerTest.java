package com.example.boilerplate.member.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.boilerplate.common.type.ApiStatus;
import com.example.boilerplate.member.dto.MemberDto;
import com.example.boilerplate.member.service.MemberService;
import com.example.boilerplate.sign.dto.SignDto;
import com.example.boilerplate.sign.service.SignService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@AutoConfigureMockMvc
@ActiveProfiles("local")
@SpringBootTest
class MemberControllerTest {

  private static final String MEMBER_BASE_URL = "/member";
  private static final String MEMBERS_BASE_URL = "/members";

  private static final long MEMBER_ID_ADMIN = 1L;
  private static final String MEMBER_EMAIL_ADMIN = "admin@example.com";
  private static final String MEMBER_PASSWORD_ADMIN = "password1!";
  private static final String MEMBER_NAME_ADMIN = "admin@example.com";

  private static final String MEMBER_EMAIL_PREFIX = "tester";
  private static final String MEMBER_NAME_PREFIX = "테스터";
  private static final String MEMBER_PASSWORD = "password1!";

  private static final String UPDATED_MEMBER_EMAIL = "tester2@example.com";
  private static final String UPDATED_MEMBER_NAME = "테스터2";

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_TOKEN_PREFIX = "Bearer ";

  private static final String PATH_STATUS_CODE = "$.statusCode";
  private static final String PATH_MESSAGE = "$.message";
  private static final String PATH_DATA_ID = "$.data.id";
  private static final String PATH_DATA_EMAIL = "$.data.email";
  private static final String PATH_DATA_NAME = "$.data.name";
  private static final String PATH_DATA_ROLE = "$.data.role";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private MemberService memberService;

  @Autowired
  private SignService signService;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private EntityManager entityManager;

  private String token;

  @BeforeEach
  void setUp() {
    this.token = getToken();
  }

  private String getToken() {
    SignDto.SignInResponse signInResponse = signService.signIn(
        SignDto.SignInRequest.of(MEMBER_EMAIL_ADMIN, MEMBER_PASSWORD_ADMIN)
    );
    return signInResponse.getToken();
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("회원 목록 조회")
  @Nested
  class TestGetMembers {

    @BeforeEach
    void setUp() {
      setUpMemberList();
      clearPersistenceContext();
    }

    @Order(1)
    @DisplayName("회원 목록 조회 성공")
    @Transactional
    @Test
    void testGetMemberListSuccess() throws Exception {

      // When
      String memberEmail = MEMBER_EMAIL_PREFIX + "_1";
      String memberName = MEMBER_NAME_PREFIX + "_1";
      ResultActions resultActions = mockMvc.perform(
          get(MEMBERS_BASE_URL)
              .header(AUTHORIZATION_HEADER, BEARER_TOKEN_PREFIX + token)
              .param("email", memberEmail)
              .param("name", memberName)
      );

      // Then
      resultActions
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath(PATH_STATUS_CODE).value(ApiStatus.OK.getCode()))
          .andExpect(jsonPath(PATH_MESSAGE).value(ApiStatus.OK.getMessage()))
          .andDo(print());
    }
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("회원 조회")
  @Nested
  class TestGetMemberById {

    @Order(1)
    @DisplayName("회원 조회 성공")
    @Transactional
    @Test
    void testGetMemberByIdSuccess() throws Exception {

      // When
      ResultActions resultActions = mockMvc.perform(
          get(MEMBER_BASE_URL + "/" + MEMBER_ID_ADMIN)
              .header(AUTHORIZATION_HEADER, BEARER_TOKEN_PREFIX + token)
      );

      // Then
      resultActions
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath(PATH_STATUS_CODE).value(ApiStatus.OK.getCode()))
          .andExpect(jsonPath(PATH_MESSAGE).value(ApiStatus.OK.getMessage()))
          .andExpect(jsonPath(PATH_DATA_ID).value(MEMBER_ID_ADMIN))
          .andExpect(jsonPath(PATH_DATA_EMAIL).value(MEMBER_EMAIL_ADMIN))
          .andExpect(jsonPath(PATH_DATA_NAME).value(MEMBER_NAME_ADMIN))
          .andDo(print());
    }
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("회원 추가")
  @Nested
  class TestInsertMember {

    @Order(1)
    @DisplayName("회원 추가 성공")
    @Transactional
    @Test
    void testInsertMemberSuccess() throws Exception {

      // Given
      String uniqueId = UUID.randomUUID().toString().substring(0, 8);
      String memberEmail = MEMBER_EMAIL_PREFIX + uniqueId;
      String memberName = MEMBER_NAME_PREFIX + uniqueId;
      MemberDto.InsertRequest insertMemberRequest =
          MemberDto.InsertRequest.of(memberEmail, memberName, MEMBER_PASSWORD);

      // When
      ResultActions resultActions = mockMvc.perform(
          post(MEMBER_BASE_URL)
              .header(AUTHORIZATION_HEADER, BEARER_TOKEN_PREFIX + token)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(insertMemberRequest))
      );

      // Then
      resultActions
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath(PATH_STATUS_CODE).value(ApiStatus.OK.getCode()))
          .andExpect(jsonPath(PATH_MESSAGE).value(ApiStatus.OK.getMessage()))
          .andExpect(jsonPath(PATH_DATA_EMAIL).value(memberEmail))
          .andExpect(jsonPath(PATH_DATA_NAME).value(memberName))
          .andDo(print());
    }
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("회원 수정")
  @Nested
  class TestUpdateMember {

    @Order(1)
    @DisplayName("회원 수정 성공")
    @Transactional
    @Test
    void testUpdateMemberSuccess() throws Exception {

      // Given
      String uniqueId = UUID.randomUUID().toString().substring(0, 8);
      String memberEmail = MEMBER_EMAIL_PREFIX + uniqueId;
      String memberName = MEMBER_NAME_PREFIX + uniqueId;
      Long memberId = saveMemberAndReturnId(memberEmail, memberName, MEMBER_PASSWORD);
      MemberDto.UpdateRequest updateMemberRequest =
          MemberDto.UpdateRequest.of(memberId, UPDATED_MEMBER_EMAIL, UPDATED_MEMBER_NAME);

      // When
      ResultActions resultActions = mockMvc.perform(
          put(MEMBER_BASE_URL)
              .header(AUTHORIZATION_HEADER, BEARER_TOKEN_PREFIX + token)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(updateMemberRequest))
      );

      // Then
      resultActions
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath(PATH_STATUS_CODE).value(ApiStatus.OK.getCode()))
          .andExpect(jsonPath(PATH_MESSAGE).value(ApiStatus.OK.getMessage()))
          .andExpect(jsonPath(PATH_DATA_ID).value(memberId))
          .andExpect(jsonPath(PATH_DATA_EMAIL).value(UPDATED_MEMBER_EMAIL))
          .andExpect(jsonPath(PATH_DATA_NAME).value(UPDATED_MEMBER_NAME))
          .andDo(print());
    }
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("회원 삭제")
  @Nested
  class TestDeleteMember {

    @Order(1)
    @DisplayName("회원 삭제 성공")
    @Transactional
    @Test
    void testDeleteMemberSuccess() throws Exception {

      // When
      String uniqueId = UUID.randomUUID().toString().substring(0, 8);
      String memberEmail = MEMBER_EMAIL_PREFIX + uniqueId;
      String memberName = MEMBER_NAME_PREFIX + uniqueId;
      Long memberId = saveMemberAndReturnId(memberEmail, memberName, MEMBER_PASSWORD);
      ResultActions resultActions = mockMvc.perform(
          delete(MEMBER_BASE_URL + "/" + memberId)
              .header(AUTHORIZATION_HEADER, BEARER_TOKEN_PREFIX + token)
      );

      // Then
      resultActions
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath(PATH_STATUS_CODE).value(ApiStatus.OK.getCode()))
          .andExpect(jsonPath(PATH_MESSAGE).value(ApiStatus.OK.getMessage()))
          .andDo(print());
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
      memberService.insertMember(
          MemberDto.InsertRequest.of(memberEmail, memberName, MEMBER_PASSWORD));
    }
  }

  @Disabled
  private Long saveMemberAndReturnId(String email, String name, String password) {
    return memberService.insertMember(MemberDto.InsertRequest.of(email, name, password)).getId();
  }
}
