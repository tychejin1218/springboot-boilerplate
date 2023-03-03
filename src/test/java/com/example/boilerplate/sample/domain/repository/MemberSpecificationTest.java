package com.example.boilerplate.sample.domain.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;

import com.example.boilerplate.sample.domain.entity.Member;
import com.example.boilerplate.sample.domain.entity.Todo;
import java.util.List;
import javax.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@ActiveProfiles("local")
@Disabled
class MemberSpecificationTest {

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private TodoRepository todoRepository;

  @Autowired
  EntityManager entityManager;

  @Transactional
  @DisplayName("Spec(likeName)을 사용하여 Member 목록을 조회")
  @Test
  void testFindAllSpecLikeName() {

    // Given
    setUpMembers();

    // When
    List<Member> members = memberRepository.findAll(
        MemberSpecification.likeName("test")
    );

    // Then
    log.debug("members:[{}]", members);
    assertFalse(members.isEmpty());
  }

  @Transactional
  @DisplayName("Spec(likeEmail)을 사용하여 Member 목록을 조회")
  @Test
  void testFindAllSpecLikeEmail() {

    // Given
    setUpMembers();

    // When
    List<Member> members = memberRepository.findAll(
        MemberSpecification.likeEmail("gmail")
    );

    // Then
    log.debug("members:[{}]", members);
    assertFalse(members.isEmpty());
  }

  @Transactional
  @DisplayName("Spec(likeName)을 사용할 때 정렬 조건(Sort)를 추가하여 Member 목록을 조회")
  @Test
  void testFindAllSpecLikeNameSort() {

    // Given
    setUpMembers();
    Sort sort = Sort.by(Order.desc("id"));

    // When
    List<Member> members = memberRepository.findAll(
        MemberSpecification.likeName("test"),
        sort
    );

    // Then
    log.debug("members:[{}]", members);
    assertFalse(members.isEmpty());
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

      if(a % 3 == 0){
        name = "admin" + a;
      } else {
        name = "test" + a;
      }

      if (a % 2 == 0) {
        email = name + "@naver.com";
      } else {
        email = name + "@gmail.com";
      }
      memberId = insertMember(name, email);

      for (int b = 1; b <= 5; b++) {
        title = "Title Test" + b;
        description = "Description Test" + b;
        if (b % 2 == 0) {
          completed = true;
        } else {
          completed = false;
        }
        insertTodo(memberId, title, description, completed);
      }
    }

    entityManager.flush();
    entityManager.clear();
  }

  /**
   * Member 저장
   */
  @Disabled
  Long insertMember(
      String name,
      String email) {
    return memberRepository.save(
        Member.builder()
            .name(name)
            .email(email)
            .build()
    ).getId();
  }

  /**
   * To-Do 저장
   */
  @Disabled
  Long insertTodo(
      Long memberId,
      String title,
      String description,
      Boolean completed) {
    return todoRepository.save(
        Todo.builder()
            .member(Member.builder()
                .id(memberId)
                .build())
            .title(title)
            .description(description)
            .completed(completed)
            .build()
    ).getId();
  }
}
