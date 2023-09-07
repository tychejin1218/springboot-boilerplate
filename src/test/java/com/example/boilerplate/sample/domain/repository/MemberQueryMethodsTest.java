package com.example.boilerplate.sample.domain.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;

import com.example.boilerplate.sample.domain.entity.Member;
import com.example.boilerplate.sample.domain.entity.Todo;
import java.util.List;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings({
    "PMD.AvoidDuplicateLiterals"
})
@Slf4j
@SpringBootTest
@ActiveProfiles("local")
@Disabled
class MemberQueryMethodsTest {

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private TodoRepository todoRepository;

  @Autowired
  private EntityManager entityManager;

  @Order(1)
  @Transactional
  @DisplayName("Query Method을 사용하여 Member 목록을 조회")
  @Test
  void testFindAllByNameContainingIgnoreCaseAndEmailContainingIgnoreCaseOrderByIdDesc() {

    // Given
    setUpMembers();
    entityManager.flush();
    entityManager.clear();

    // When
    List<Member> members = memberRepository
        .findAllByNameContainingIgnoreCaseAndEmailContainingIgnoreCaseOrderByIdDesc(
            "test",
            "gmail.com"
        );

    // Then
    log.debug("members:[{}]", members);
    assertFalse(members.isEmpty());
  }

  @Order(2)
  @Transactional
  @DisplayName("Query Method을 사용할 때 정렬 조건(Sort)를 추가하여 Member 목록을 조회")
  @Test
  void testFindAllByNameContainingIgnoreCaseAndEmailContainingIgnoreCase() {

    // Given
    setUpMembers();
    entityManager.flush();
    entityManager.clear();

    Sort sort = Sort.by(org.springframework.data.domain.Sort.Order.desc("id"));

    // When
    List<Member> members = memberRepository
        .findAllByNameContainingIgnoreCaseAndEmailContainingIgnoreCase(
            "test",
            "gmail.com",
            sort
        );

    // Then
    log.debug("members:[{}]", members);
    assertFalse(members.isEmpty());
  }

  @Order(3)
  @Transactional
  @DisplayName("Query Method을 사용할 때 페이징(Pageable)을 추가하여 Member 목록 조회")
  @Test
  void testFindAllByNameContainsAndEmailContainsOrderOrderByIdDescPage() {

    // Given
    setUpMembers();
    entityManager.flush();
    entityManager.clear();

    // When
    Page<Member> memberPage = memberRepository.findAllByNameContainsAndEmailContainsOrderByIdDesc(
        "test",
        "gmail.com",
        PageRequest.of(0, 5)
    );

    // Then
    log.debug("Content:[{}]", memberPage.getContent());
    log.debug("Size:[{}]", memberPage.getSize());
    log.debug("TotalPages:[{}]", memberPage.getTotalPages());
    log.debug("TotalElements:[{}]", memberPage.getTotalElements());
    log.debug("NextPageable:[{}]", memberPage.nextPageable());
    assertFalse(memberPage.getContent().isEmpty());
  }

  /**
   * Member 목록 설정
   */
  @Disabled
  void setUpMembers() {

    Long memberId;
    String name;
    String email;

    String title;
    String description;
    Boolean completed;

    for (int a = 1; a <= 10; a++) {

      name = "test" + String.format("%02d", a);
      if (a % 2 == 0) {
        email = name + "@naver.com";
      } else {
        email = name + "@gmail.com";
      }
      memberId = memberRepository.save(
          Member.builder()
              .name(name)
              .email(email)
              .build()
      ).getId();

      for (int b = 1; b <= 5; b++) {
        title = "Title Test" + String.format("%02d", b);
        description = "Description Test" + String.format("%02d", b);
        if (b % 2 == 0) {
          completed = true;
        } else {
          completed = false;
        }
        todoRepository.save(
            Todo.builder()
                .member(Member.builder()
                    .id(memberId)
                    .build())
                .title(title)
                .description(description)
                .completed(completed)
                .build()
        );
      }
    }
  }
}
