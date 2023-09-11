package com.example.boilerplate.sign.dto;

import jakarta.persistence.Entity;
import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CustomeUserDetailDto implements UserDetails {

  private String username;
  private String password;
  private boolean isEnabled;
  private boolean isAccountNonExpired;
  private boolean isAccountNonLocked;
  private boolean isCredentialsNonExpired;
  private Collection<? extends GrantedAuthority> authorities;

  /**
   * 해당 유저의 권한 목록
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  /**
   * 비밀번호
   */
  @Override
  public String getPassword() {
    return password;
  }


  /**
   * PK값
   */
  @Override
  public String getUsername() {
    return username;
  }

  /**
   * 계정 만료 여부-  true : 만료 안됨 false : 만료
   *
   * @return
   */
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  /**
   * 계정 잠김 여부 - true : 잠기지 않음 false : 잠김
   *
   * @return
   */
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  /**
   * 비밀번호 만료 여부 - true : 만료 안됨 false : 만료
   *
   * @return
   */
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }


  /**
   * 사용자 활성화 여부 - ture : 활성화 false : 비활성화
   *
   * @return
   */
  @Override
  public boolean isEnabled() {
    return true;
  }
}
