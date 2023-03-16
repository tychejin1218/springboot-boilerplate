package com.example.boilerplate.sample.domain.repository;

import com.example.boilerplate.sample.domain.entity.Member;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MemberRepository extends
    JpaRepository<Member, Long>,
    JpaSpecificationExecutor<Member> {

  /**
   * Query Method을 사용하여 Member 목록 조회
   */
  List<Member> findAllByNameContainingIgnoreCaseAndEmailContainingIgnoreCaseOrderByIdDesc(
      String name,
      String email
  );

  /**
   * Query Method을 사용할 때 정렬 조건(Sort)를 추가하여 Member 목록을 조회
   */
  List<Member> findAllByNameContainingIgnoreCaseAndEmailContainingIgnoreCase(
      String name,
      String email,
      Sort sort
  );

  /**
   * Query Method을 사용할 때 페이징(Pageable)을 추가하여 Member 목록 조회
   */
  Page<Member> findAllByNameContainsAndEmailContainsOrderByIdDesc(
      String name,
      String email,
      Pageable pageable
  );

  /**
   * Query Method을 사용할 때 페이징(Pageable)을 추가하여 Member 목록 조회
   */
  Page<Member> findAllByNameContainsAndEmailContains(
      String name,
      String email,
      Pageable pageable
  );
}
