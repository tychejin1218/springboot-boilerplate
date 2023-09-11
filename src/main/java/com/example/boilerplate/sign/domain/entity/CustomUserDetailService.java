package com.example.boilerplate.sign.domain.entity;

import com.example.boilerplate.common.exception.ApiException;
import com.example.boilerplate.common.type.ApiStatus;
import com.example.boilerplate.sample.domain.entity.Member;
import com.example.boilerplate.sample.domain.repository.MemberRepository;
import com.example.boilerplate.sign.dto.CustomeUserDetailDto;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new ApiException(ApiStatus.INVALID_REQUEST));

    return CustomeUserDetailDto.builder()
        .username(member.getEmail())
        .password(member.getPassword())
        .authorities(getAuthorities(member.getRole()))
        .isEnabled(true)
        .isAccountNonLocked(true)
        .isAccountNonLocked(true)
        .isCredentialsNonExpired(true)
        .build();
  }

  public Collection<GrantedAuthority> getAuthorities(String roles) {
    return Arrays.stream(roles.split(",")).toList()
        .stream()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
  }
}
