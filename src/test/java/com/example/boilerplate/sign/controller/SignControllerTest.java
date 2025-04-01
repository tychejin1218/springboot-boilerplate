package com.example.boilerplate.sign.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.boilerplate.common.type.ApiStatus;
import com.example.boilerplate.sign.dto.SignDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
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
class SignControllerTest {

  private static final String SIGN_UP_URL = "/sign/signup";
  private static final String SIGN_IN_URL = "/sign/signin";

  private static final String MEMBER_EMAIL_PREFIX = "tester";
  private static final String MEMBER_EMAIL_SUFFIX = "@example.com";
  private static final String MEMBER_PASSWORD = "password1!";
  private static final String MEMBER_NAME_PREFIX = "테스터";

  private static final String PATH_STATUS_CODE = "$.statusCode";
  private static final String PATH_MESSAGE = "$.message";
  private static final String PATH_DATA_EMAIL = "$.data.email";
  private static final String PATH_DATA_NAME = "$.data.name";
  private static final String PATH_DATA_TOKEN = "$.data.token";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("/sign/signup: 회원가입")
  @Nested
  class TestSignUp {

    @Order(1)
    @DisplayName("회원가입 성공")
    @Transactional
    @Test
    void testSignUpSuccess() throws Exception {

      // Given
      String uniqueId = UUID.randomUUID().toString().substring(0, 8);
      String memberName = MEMBER_NAME_PREFIX + uniqueId;
      String memberEmailPrefix = MEMBER_EMAIL_PREFIX + uniqueId;
      String memberEmail = memberEmailPrefix + MEMBER_EMAIL_SUFFIX;
      SignDto.SignUpRequest signUpRequest =
          SignDto.SignUpRequest.of(memberEmail, memberName, MEMBER_PASSWORD);

      // When
      ResultActions resultActions = mockMvc.perform(
          post(SIGN_UP_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(signUpRequest))
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
  @DisplayName("/sign/signin: 로그인")
  @Nested
  class TestSignIn {

    @Order(1)
    @DisplayName("로그인 성공")
    @Transactional
    @Test
    void testSignInSuccess() throws Exception {

      // Given
      String memberEmail = MEMBER_EMAIL_PREFIX + MEMBER_EMAIL_SUFFIX;
      SignDto.SignInRequest signInRequest =
          SignDto.SignInRequest.of(memberEmail, MEMBER_PASSWORD);

      // When
      ResultActions resultActions = mockMvc.perform(
          post(SIGN_IN_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(signInRequest))
      );

      // Then
      resultActions
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath(PATH_STATUS_CODE).value(ApiStatus.OK.getCode()))
          .andExpect(jsonPath(PATH_MESSAGE).value(ApiStatus.OK.getMessage()))
          .andExpect(jsonPath(PATH_DATA_TOKEN).isNotEmpty())
          .andDo(print());
    }
  }
}
