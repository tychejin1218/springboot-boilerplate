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
   * 이메일로 Member 조회
   *
   * @param email 이메일
   * @return MemberEntity(Optional)
   */
  Optional<MemberEntity> findByEmail(String email);

  /**
   * 이름과 이메일을 포함하는 Member 목록을 ID 내림차순으로 조회(부분 문자열, 대소문자 무시)
   *
   * @param name  이름
   * @param email 이메일
   * @return MemberEntity 목록
   */
  List<MemberEntity> findAllByNameContainingIgnoreCaseAndEmailContainingIgnoreCaseOrderByIdDesc(
      String name,
      String email
  );

  /**
   * 이름과 이메일을 포함하는 Member 목록을 정렬 조건에 따라 조회(부분 문자열, 대소문자 무시)
   *
   * @param name  이름
   * @param email 이메일
   * @param sort  정렬 조건
   * @return MemberEntity 목록
   */
  List<MemberEntity> findAllByNameContainingIgnoreCaseAndEmailContainingIgnoreCase(
      String name,
      String email,
      Sort sort
  );

  /**
   * 이름과 이메일을 포함하는 Member 목록을 페이징하여 ID 내림차순으로 조회
   *
   * @param name     이름
   * @param email    이메일
   * @param pageable 페이징 정보
   * @return 페이징된 MemberEntity(Page)
   */
  Page<MemberEntity> findAllByNameContainsAndEmailContainsOrderByIdDesc(
      String name,
      String email,
      Pageable pageable
  );

  /**
   * 이름과 이메일을 포함하는 Member 목록을 페이징하여 조회
   *
   * @param name     이름
   * @param email    이메일
   * @param pageable 페이징 정보
   * @return 페이징된 MemberEntity(Page)
   */
  Page<MemberEntity> findAllByNameContainsAndEmailContains(
      String name,
      String email,
      Pageable pageable
  );
}
