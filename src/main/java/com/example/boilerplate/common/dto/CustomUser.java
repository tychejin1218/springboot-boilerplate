package com.example.boilerplate.common.dto;

import com.example.boilerplate.domain.entity.MemberEntity;
import java.io.Serial;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Getter
@Setter
@ToString
public class CustomUser extends User {

  @Serial
  private static final long serialVersionUID = 2766590282782490762L;

  private transient MemberEntity member;

  public CustomUser(MemberEntity member) {
    super(member.getName(), member.getPassword(), getAuthorities(member.getRole()));
    this.member = member;
  }

  public static Collection<GrantedAuthority> getAuthorities(String roles) {
    return Arrays.stream(roles.split(",")).toList()
        .stream()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
  }
}
