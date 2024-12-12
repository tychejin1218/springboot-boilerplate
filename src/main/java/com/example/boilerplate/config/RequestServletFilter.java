package com.example.boilerplate.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.springframework.stereotype.Component;

/**
 * {@link HttpServletRequest}를 {@link RequestServletWrapper}로 래핑하여 요청 본문(request body)을 여러 번 읽을 수
 * 있도록 처리하는 필터 클래스
 *
 * <p>모든 HTTP 요청이 해당 필터를 거치며, 요청 본문 데이터를 재사용할 수 있도록 {@link RequestServletWrapper}로 래핑
 */
@Component
public class RequestServletFilter implements Filter {

  @Override
  public void doFilter(
      ServletRequest request,
      ServletResponse response,
      FilterChain chain) throws IOException, ServletException {
    chain.doFilter(new RequestServletWrapper((HttpServletRequest) request), response);
  }
}
