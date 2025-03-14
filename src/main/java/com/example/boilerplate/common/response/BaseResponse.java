package com.example.boilerplate.common.response;

import com.example.boilerplate.common.type.ApiStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@SuppressWarnings("PMD.ImmutableField")
@Getter
@NoArgsConstructor
public class BaseResponse<T> {

  private String statusCode;
  private String message;
  private T data;

  /**
   * API 응답 생성자
   *
   * @param statusCode 상태 코드
   * @param message    상태 메시지
   * @param data       응답 데이터
   */
  @Builder
  public BaseResponse(String statusCode, String message, T data) {
    this.statusCode = StringUtils.hasText(statusCode) ? statusCode : ApiStatus.OK.getCode();
    this.message = StringUtils.hasText(message) ? message : ApiStatus.OK.getMessage();
    this.data = data;
  }

  /**
   * 공통적으로 BaseResponse를 생성
   *
   * @param statusCode 상태 코드
   * @param message    메시지
   * @param data       응답 데이터
   * @param <T>        데이터 타입
   * @return BaseResponse 객체
   */
  private static <T> BaseResponse<T> buildResponse(String statusCode, String message, T data) {
    return BaseResponse.<T>builder()
        .statusCode(statusCode)
        .message(message)
        .data(data)
        .build();
  }

  /**
   * 성공 응답 객체 생성 (기본 상태 코드와 기본 메시지 포함)
   *
   * @param body 응답 데이터
   * @param <T>  데이터 타입
   * @return 성공 응답 BaseResponse 객체
   */
  public static <T> BaseResponse<T> ok(T body) {
    return buildResponse(ApiStatus.OK.getCode(), ApiStatus.OK.getMessage(), body);
  }

  /**
   * 사용자 지정 메시지의 성공 응답 객체 생성
   *
   * @param message 성공 메시지
   * @param <T>     데이터 타입
   * @return 성공 응답 BaseResponse 객체
   */
  public static <T> BaseResponse<T> okWithMessage(String message) {
    return buildResponse(ApiStatus.OK.getCode(), message, null);
  }

  /**
   * ApiStatus를 사용하여 상태 코드와 메시지가 포함된 응답 객체 생성
   *
   * @param apiStatus ApiStatus 객체
   * @param <T>       데이터 타입
   * @return 성공 응답 BaseResponse 객체
   */
  public static <T> BaseResponse<T> okWithStatus(ApiStatus apiStatus) {
    return buildResponse(apiStatus.getCode(), apiStatus.getMessage(), null);
  }

  /**
   * 메시지 변경된 새로운 BaseResponse 반환 (상태 코드와 데이터는 유지)
   *
   * @param message 새로운 메시지
   * @return 수정된 BaseResponse 객체
   */
  public BaseResponse<T> withMessage(String message) {
    return buildResponse(this.statusCode, message, this.data);
  }

  /**
   * 상태 코드 변경된 새로운 BaseResponse 반환 (메시지와 데이터 유지)
   *
   * @param statusCode 새로운 상태 코드
   * @return 수정된 BaseResponse 객체
   */
  public BaseResponse<T> withStatusCode(String statusCode) {
    return buildResponse(statusCode, this.message, this.data);
  }
}
