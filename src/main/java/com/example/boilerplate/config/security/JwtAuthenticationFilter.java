package com.example.boilerplate.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.GenericFilterBean;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

  private final JwtTokenProvider jwtTokenProvider;

  /**
   * HTTP 요청에 대한 JWT Token을 검사하고, 유효한 경우 인증 정보를 설정
   *
   * @param request     ServletRequest
   * @param response    ServletResponse
   * @param filterChain FilterChain
   * @throws IOException
   * @throws ServletException
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {

    String token = jwtTokenProvider.getResolveToken((HttpServletRequest) request);
    if (token != null && jwtTokenProvider.validateToken(token)) {
      Authentication auth = jwtTokenProvider.getAuthentication(token);
      // Spring Security의 SecurityContext에 인증 정보를 설정
      SecurityContextHolder.getContext().setAuthentication(auth);

      Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      UserDetails userDetails = (UserDetails) principal;
      log.info("username:{}, password:{}", userDetails.getUsername(), userDetails.getPassword());
    }

    filterChain.doFilter(request, response);
  }
}
