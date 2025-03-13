package com.example.boilerplate.common.response;


import com.example.boilerplate.common.type.ApiStatus;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

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
    this.message = StringUtils.hasText(message) ? message
        : ApiStatus.getByCode(statusCode).getMessage();
    this.method = method;
    this.path = path;
    this.timestamp = StringUtils.hasText(timestamp) ? timestamp
        : LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
  }
}
