package com.example.boilerplate.common.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.example.boilerplate.common.type.ApiStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ApiExceptionTest {

  @Test
  @DisplayName("HTTP 상태 코드, Api 상태 코드, 에러 메시지를 받는 생성자 테스트")
  void testGivenHttpStatusApiStatusAndMessage() {

    // given
    HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    ApiStatus apiStatus = ApiStatus.INVALID_REQUEST;
    String message = "Custom message";

    // when
    ApiException exception = new ApiException(httpStatus, apiStatus, message);

    // then
    assertAll(
        () -> assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST),
        () -> assertThat(exception.getStatus()).isEqualTo(ApiStatus.INVALID_REQUEST),
        () -> assertThat(exception.getMessage()).isEqualTo("Custom message")
    );
  }

  @Test
  @DisplayName("HTTP 상태 코드와 Api 상태 코드를 받는 생성자 테스트")
  void testGivenHttpStatusAndApiStatus() {

    // given
    HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    ApiStatus apiStatus = ApiStatus.INVALID_REQUEST;

    // when
    ApiException exception = new ApiException(httpStatus, apiStatus);

    // then
    assertAll(
        () -> assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST),
        () -> assertThat(exception.getStatus()).isEqualTo(ApiStatus.INVALID_REQUEST),
        () -> assertThat(exception.getMessage()).isEqualTo(ApiStatus.INVALID_REQUEST.getMessage())
    );
  }

  @Test
  @DisplayName("HTTP 상태 코드와 에러 메시지를 받는 생성자 테스트")
  void testGivenHttpStatusAndMessage() {

    // given
    HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
    String message = "unauthorized";

    // when
    ApiException exception = new ApiException(httpStatus, message);

    // then
    assertAll(
        () -> assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED),
        () -> assertThat(exception.getStatus()).isEqualTo(ApiStatus.CUSTOM_EXCEPTION),
        () -> assertThat(exception.getMessage()).isEqualTo(message)
    );
  }

  @Test
  @DisplayName("Api 상태 코드와 에러 메시지를 받는 생성자 테스트")
  void testGivenApiStatusAndMessage() {

    // given
    ApiStatus apiStatus = ApiStatus.INTERNAL_SERVER_ERROR;
    String message = "internal server error";

    // when
    ApiException exception = new ApiException(apiStatus, message);

    // then
    assertAll(
        () -> assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR),
        () -> assertThat(exception.getStatus()).isEqualTo(apiStatus),
        () -> assertThat(exception.getMessage()).isEqualTo(message)
    );
  }

  @Test
  @DisplayName("Api 상태 코드를 받는 생성자 테스트")
  void testGivenApiStatus() {

    // given
    ApiStatus apiStatus = ApiStatus.INTERNAL_SERVER_ERROR;

    // when
    ApiException exception = new ApiException(apiStatus);

    // then
    assertAll(
        () -> assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR),
        () -> assertThat(exception.getStatus()).isEqualTo(apiStatus),
        () -> assertThat(exception.getMessage()).isEqualTo(apiStatus.getMessage())
    );
  }

  @Test
  @DisplayName("에러 메시지를 받는 생성자 테스트")
  void testGivenMessage() {

    // given
    String message = "Error Message";

    // when
    ApiException exception = new ApiException(message);

    // then
    assertAll(
        () -> assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR),
        () -> assertThat(exception.getStatus()).isEqualTo(ApiStatus.CUSTOM_EXCEPTION),
        () -> assertThat(exception.getMessage()).isEqualTo(message)
    );
  }
}
