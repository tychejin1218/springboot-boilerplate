package com.example.boilerplate.config.security;

import com.example.boilerplate.common.response.ErrorResponse;
import com.example.boilerplate.common.type.ApiStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

  /**
   * 권한 거부 시 호출되는 메서드
   *
   * @param request               HttpServletRequest 객체
   * @param response              HttpServletResponse 객체
   * @param accessDeniedException 권한 거부 예외 객체
   * @throws IOException 입출력 예외
   */
  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException {
    log.error("No Authorization Request URI: {}", request.getRequestURI(), accessDeniedException);
    ObjectMapper objectMapper = new ObjectMapper();
    response.setStatus(HttpStatus.FORBIDDEN.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");
    objectMapper.writeValue(response.getWriter(), ErrorResponse.builder()
        .statusCode(ApiStatus.UNAUTHORIZED.getCode())
        .method(request.getMethod())
        .message(ApiStatus.UNAUTHORIZED.getMessage())
        .path(request.getRequestURI())
        .build());
  }
}
