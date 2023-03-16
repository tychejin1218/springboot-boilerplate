package com.example.boilerplate.web.reponse;

import com.example.boilerplate.common.type.ApiStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
public class BaseResponse<T> {

  private String statusCode;
  private String message;
  private T data;

  @Builder
  public BaseResponse(
      String statusCode,
      String message,
      T data
  ) {
    this.statusCode = statusCode;
    this.message =
        StringUtils.isNotBlank(message) ? message
            : ApiStatus.valueOfStatusCode(statusCode).getMessage();
    this.data = data;
  }
}
