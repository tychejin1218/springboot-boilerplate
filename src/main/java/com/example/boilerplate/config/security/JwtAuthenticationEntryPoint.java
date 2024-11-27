package com.example.boilerplate.config.security;


import com.example.boilerplate.common.type.ApiStatus;
import com.example.boilerplate.common.reponse.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException) throws IOException {
    log.error("Not Authentication Request URI: {}", request.getRequestURI(), authException);
    ObjectMapper objectMapper = new ObjectMapper();
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
