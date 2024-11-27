package com.example.boilerplate.common.service;

import com.example.boilerplate.common.dto.CustomUser;
import com.example.boilerplate.common.exception.ApiException;
import com.example.boilerplate.common.type.ApiStatus;
import com.example.boilerplate.domain.entity.MemberEntity;
import com.example.boilerplate.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String email) {
    MemberEntity member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new ApiException(ApiStatus.INVALID_REQUEST));
    log.debug("member : {}", member);
    return new CustomUser(member);
  }
}
