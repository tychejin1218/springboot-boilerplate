package com.example.boilerplate.sample.domain.repository;

import com.example.boilerplate.sample.domain.entity.Member;
import org.springframework.data.jpa.domain.Specification;

public class MemberSpecification {

  public static Specification<Member> likeName(String name) {
    return (root, query, criteriaBuilder)
        -> criteriaBuilder.like(root.get("name"), "%" + name + "%");
  }

  public static Specification<Member> likeEmail(String email) {
    return (root, query, criteriaBuilder)
        -> criteriaBuilder.like(root.get("email"), "%" + email + "%");
  }
}
