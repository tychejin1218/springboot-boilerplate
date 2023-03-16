package com.example.boilerplate.web.reponse;

import com.example.boilerplate.common.type.ApiStatus;
import com.example.boilerplate.common.util.DateUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
public class ErrorResponse {

  private String statusCode;
  private String message;
  private String method;
  private String path;
  private String timestamp;

  @Builder
  ErrorResponse(
      String statusCode,
      String message,
      String method,
      String path,
      String timestamp
  ) {
    this.statusCode = statusCode;
    this.message =
        StringUtils.isNotBlank(message) ? message
            : ApiStatus.valueOfStatusCode(statusCode).getMessage();
    this.method = method;
    this.path = path;
    this.timestamp =
        StringUtils.isNotBlank(timestamp) ? timestamp
            : DateUtils.getFormatDate(DateUtils.YYYYMMDDHHMMSS);
  }
}
