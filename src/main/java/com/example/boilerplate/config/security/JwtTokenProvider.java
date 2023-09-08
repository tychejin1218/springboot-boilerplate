package com.example.boilerplate.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

  @Value("${jwt.header}")
  private String header;

  @Value("${jwt.secret-key}")
  private String secretKey;

  private final long tokenValidTime = 1000L * 60 * 1;

  public String createToken(String subject, List<String> roles) {

    Map<String, Object> headerMap = new HashMap<>();
    headerMap.put("typ", "JWT");
    headerMap.put("alg", "HS256");

    Map<String, Object> claims = new HashMap<>();
    claims.put("roles", roles);

    Date expireTime = new Date();
    expireTime.setTime(expireTime.getTime() + tokenValidTime);

    return Jwts.builder()
        .setHeader(headerMap)
        .setClaims(claims)
        .setSubject(subject)
        .setExpiration(expireTime)
        .signWith(SignatureAlgorithm.HS256, secretKey).compact();
  }

  public String getToken(HttpServletRequest request) {
    return request.getHeader(header);
  }

  public boolean validateToken(String token) {
    try {
      Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
      return !claims.getBody().getExpiration().before(new Date());
    } catch (Exception e) {
      return false;
    }
  }

  public Authentication getAuthentication(String token) {

    Claims claims = Jwts
        .parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(token)
        .getBody();

    Collection<? extends GrantedAuthority> authorities =
        Arrays.stream(claims.get(header).toString().split(","))
            .map(role -> new SimpleGrantedAuthority(role))
            .collect(Collectors.toList());

    User principal = new User(claims.getSubject(), "", authorities);

    return new UsernamePasswordAuthenticationToken(principal, token, authorities);
  }
}
