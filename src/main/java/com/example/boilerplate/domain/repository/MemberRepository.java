package com.example.boilerplate.domain.repository;

import com.example.boilerplate.domain.entity.MemberEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MemberRepository extends
    JpaRepository<MemberEntity, Long>,
    JpaSpecificationExecutor<MemberEntity> {

  /**
   * Query Method을 사용하여 Member를 조회
   *
   * @param email String
   * @return Optional&lt;Member&gt;
   */
  Optional<MemberEntity> findByEmail(String email);

  /**
   * Query Method을 사용하여 Member 목록 조회
   *
   * @param name String
   * @param email String
   * @return List&lt;Member&gt;
   */
  List<MemberEntity> findAllByNameContainingIgnoreCaseAndEmailContainingIgnoreCaseOrderByIdDesc(
      String name,
      String email
  );

  /**
   * Query Method을 사용할 때 정렬 조건(Sort)를 추가하여 Member 목록을 조회
   *
   * @param name String
   * @param email String
   * @param sort Sort
   * @return List&lt;Member&gt;
   */
  List<MemberEntity> findAllByNameContainingIgnoreCaseAndEmailContainingIgnoreCase(
      String name,
      String email,
      Sort sort
  );

  /**
   * Query Method을 사용할 때 페이징(Pageable)을 추가하여 Member 목록 조회
   *
   * @param name String
   * @param email String
   * @param pageable Pageable
   * @return Page&lt;Member&gt;
   */
  Page<MemberEntity> findAllByNameContainsAndEmailContainsOrderByIdDesc(
      String name,
      String email,
      Pageable pageable
  );

  /**
   * Query Method을 사용할 때 페이징(Pageable)을 추가하여 Member 목록 조회
   *
   * @param name String
   * @param email String
   * @param pageable String
   * @return Page&lt;Member&gt;
   */
  Page<MemberEntity> findAllByNameContainsAndEmailContains(
      String name,
      String email,
      Pageable pageable
  );
}
