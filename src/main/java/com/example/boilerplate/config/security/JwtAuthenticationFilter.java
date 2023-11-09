package com.example.boilerplate.config.security;

import com.example.boilerplate.common.dto.CustomUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;

  /**
   * HTTP 요청에 대한 JWT Token을 검사하고, 유효한 경우 인증 정보를 설정
   *
   * @param request     HttpServletRequest
   * @param response    HttpServletResponse
   * @param filterChain FilterChain
   * @throws IOException      IOException
   * @throws ServletException ServletException
   */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
      throws IOException, ServletException {

    String token = jwtTokenProvider.getResolveToken(request);
    if (token != null && jwtTokenProvider.validateToken(token)) {
      Authentication auth = jwtTokenProvider.getAuthentication(token);
      // Spring Security의 SecurityContext에 인증 정보를 설정
      SecurityContextHolder.getContext().setAuthentication(auth);

      Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      CustomUser customUser = (CustomUser) principal;
      log.debug("customUser : {}", customUser);
    }

    filterChain.doFilter(request, response);
  }
}
