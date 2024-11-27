package com.example.boilerplate.domain.repository;

import com.example.boilerplate.domain.entity.MemberEntity;
import org.springframework.data.jpa.domain.Specification;

public class MemberSpecification {

  public static Specification<MemberEntity> likeName(String name) {
    return (root, query, criteriaBuilder)
        -> criteriaBuilder.like(root.get("name"), "%" + name + "%");
  }

  public static Specification<MemberEntity> likeEmail(String email) {
    return (root, query, criteriaBuilder)
        -> criteriaBuilder.like(root.get("email"), "%" + email + "%");
  }
}
