package com.example.boilerplate.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

  private final UserDetailsService userDetailsService;

  @Value("${jwt.header}")

  private String header;

  @Value("${jwt.secret-key}")
  private String secretKey;

  private final long tokenValidTime = 1000L * 60 * 1;

  /**
   * JWT 토큰 생성
   *
   * @param subject 회원을 식별할 수 있는 유일한 값(Email)
   * @param roles   회원 권한(ROLE_ADMIN, ROLE_USER)
   * @return 생성된 JWT 토큰
   */
  public String createToken(String subject, List<String> roles) {

    Map<String, Object> headerMap = new HashMap<>();
    headerMap.put("typ", "JWT");
    headerMap.put("alg", "HS256");

    Map<String, Object> claims = new HashMap<>();
    claims.put("roles", roles);

    return Jwts.builder()
        .setHeader(headerMap)
        .setClaims(claims)
        .setSubject(subject)
        .setExpiration(new Date(System.currentTimeMillis() + tokenValidTime))
        .signWith(SignatureAlgorithm.HS256, secretKey).compact();
  }

  /**
   * Request의 Header에서 JWT 토큰 추출 - Authorization: JWT 토큰
   *
   * @param request HttpServletRequest
   * @return 추출된 JWT 토큰
   */
  public String getResolveToken(HttpServletRequest request) {
    return request.getHeader(header);
  }

  /**
   * JWT 토큰의 유효성 및 만료일 확인
   *
   * @param token JWT 토큰
   * @return 토큰이 유효한지 여부
   */
  public boolean validateToken(String token) {
    try {
      Jws<Claims> claims = Jwts.parserBuilder()
          .setSigningKey(secretKey)
          .build()
          .parseClaimsJws(token);
      return !claims.getBody().getExpiration().before(new Date());
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * JWT 토큰으로부터 인증 정보 추출
   *
   * @param token JWT 토큰
   * @return Authentication 추출된 인증 정보
   */
  public Authentication getAuthentication(String token) {
    UserDetails userDetails = userDetailsService.loadUserByUsername(getSubject(token));
    log.debug("userDetails : {}", userDetails);
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  /**
   * JWT 토큰으로부터 subject 추출
   *
   * @param token JWT 토큰
   * @return String 추출된 subject
   */
  public String getSubject(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }
}
