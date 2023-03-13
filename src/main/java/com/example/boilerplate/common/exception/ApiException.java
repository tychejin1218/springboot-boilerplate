package com.example.boilerplate.common.exception;

import com.example.boilerplate.common.type.ApiStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ApiException extends RuntimeException {

  private static final long serialVersionUID = -1179299781904521091L;
  private ApiStatus status;
  private String message;

  public ApiException(ApiStatus apiResponseCode) {
    super();
    this.status = apiResponseCode;
    this.message = apiResponseCode.getMessage();
  }

  public ApiException(String message) {
    super();
    this.status = ApiStatus.CUSTOM_EXCEPTION;
    this.message = message;
  }
}
