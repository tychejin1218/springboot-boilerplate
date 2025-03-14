package com.example.boilerplate.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.example.boilerplate.domain.entity.MemberEntity;
import com.example.boilerplate.domain.entity.TodoEntity;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("local")
@SpringBootTest
class MemberRepositoryQueryMethodTest {

  private static final String TEST_NAME = "test01";
  private static final String TEST_EMAIL = "test01@gmail.com";
  private static final String PARTIAL_NAME = "test";
  private static final String PARTIAL_EMAIL = "gmail.com";

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  TodoRepository todoRepository;

  @Order(1)
  @DisplayName("회원 이메일로 조회 시, 일치하는 회원을 반환")
  @Transactional
  @Test
  void testFindMemberByEmail() {

    // Given
    MemberEntity memberEntity = createMember(TEST_NAME, TEST_EMAIL);
    memberRepository.save(memberEntity);

    // When
    Optional<MemberEntity> optMemberEntity = memberRepository.findByEmail(TEST_EMAIL);
    log.info("Member query result: {}", optMemberEntity);

    // Then
    assertAll(
        () -> assertThat(optMemberEntity).isPresent(),
        () -> assertThat(optMemberEntity.get().getEmail()).isEqualTo(TEST_EMAIL)
    );
  }

  @Order(2)
  @DisplayName("회원 이름과 이메일 검색 시, ID 내림차순으로 정렬된 결과 반환")
  @Transactional
  @Test
  void testFindMembersByNameAndEmailSortedByIdDescending() {

    // Given
    createSampleMembersWithTodos(10, 5);

    // When
    List<MemberEntity> memberEntityList = memberRepository
        .findAllByNameContainingIgnoreCaseAndEmailContainingIgnoreCaseOrderByIdDesc(
            PARTIAL_NAME, PARTIAL_EMAIL);

    log.info("Query result: {}", memberEntityList);

    // Then
    assertAll(
        () -> assertThat(memberEntityList).isNotEmpty(),
        () -> assertThat(memberEntityList.get(0).getName()).containsIgnoringCase(PARTIAL_NAME)
    );
  }

  @Order(3)
  @DisplayName("커스텀 정렬 조건으로 회원 검색 시, 정렬된 결과 반환")
  @Transactional
  @Test
  void testFindMembersWithCustomSort() {

    // Given
    createSampleMembersWithTodos(10, 5);

    Sort sort = Sort.by(Sort.Order.desc("id"));

    // When
    List<MemberEntity> memberEntityList = memberRepository
        .findAllByNameContainingIgnoreCaseAndEmailContainingIgnoreCase(
            PARTIAL_NAME, PARTIAL_EMAIL, sort);

    log.info("Sorted member list: {}", memberEntityList);

    // Then
    assertAll(
        () -> assertThat(memberEntityList).isNotEmpty(),
        () -> assertThat(memberEntityList.get(0).getName()).contains(PARTIAL_NAME)
    );
  }

  @Order(4)
  @DisplayName("페이징 조건과 ID 내림차순으로 회원 검색 시, 페이징된 결과 반환")
  @Transactional
  @Test
  void testFindMembersByPagingWithIdDescending() {

    // Given
    createSampleMembersWithTodos(10, 5);

    Pageable pageable = PageRequest.of(0, 5);

    // When
    Page<MemberEntity> pageMemberEntity = memberRepository
        .findAllByNameContainsAndEmailContainsOrderByIdDesc(PARTIAL_NAME, PARTIAL_EMAIL, pageable);

    log.info("Paged result: {}, Total elements: {}", pageMemberEntity.getContent(),
        pageMemberEntity.getTotalElements());

    // Then
    assertAll(
        () -> assertThat(pageMemberEntity.getContent()).isNotEmpty(),
        () -> assertThat(pageMemberEntity.getTotalElements()).isEqualTo(5),
        () -> assertThat(pageMemberEntity.getContent().get(0).getName()).contains(PARTIAL_NAME)
    );
  }

  @Order(5)
  @DisplayName("페이징 조건으로 검색 시, 결과 목록 반환")
  @Transactional
  @Test
  void testFindMembersByPaging() {

    // Given
    createSampleMembersWithTodos(10, 5);

    Pageable pageable = PageRequest.of(0, 5);

    // When
    Page<MemberEntity> pageMemberEntity = memberRepository
        .findAllByNameContainsAndEmailContains(PARTIAL_NAME, PARTIAL_EMAIL, pageable);

    log.info("Paged result: {}, Total elements: {}", pageMemberEntity.getContent(),
        pageMemberEntity.getTotalElements());

    // Then
    assertAll(
        () -> assertThat(pageMemberEntity).isNotNull(),
        () -> assertThat(pageMemberEntity.getTotalElements()).isGreaterThan(0),
        () -> assertThat(pageMemberEntity.getSize()).isEqualTo(5)
    );
  }

  @Disabled
  private void createSampleMembersWithTodos(int memberCount, int todosPerMember) {
    for (int i = 1; i <= memberCount; i++) {
      String name = PARTIAL_NAME + i;
      String email = i % 2 == 0 ? name + "@naver.com" : name + "@gmail.com";
      MemberEntity savedMember = memberRepository.save(createMember(name, email));
      createSampleTodos(savedMember, todosPerMember);
    }
  }

  @Disabled
  private void createSampleTodos(MemberEntity member, int count) {
    for (int i = 1; i <= count; i++) {
      String title = "할 일 " + i;
      String description = "할 일 설명 " + i;
      todoRepository.save(
          TodoEntity.builder()
              .member(member)
              .title(title)
              .description(description)
              .completed(i % 2 == 0)
              .build());
    }
  }

  @Disabled
  private MemberEntity createMember(String name, String email) {
    return MemberEntity.builder()
        .name(name)
        .email(email)
        .build();
  }
}
