package com.example.boilerplate.domain.repository;

import com.example.boilerplate.domain.entity.TodoEntity;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends
    JpaRepository<TodoEntity, Long> {

  String ENTITY_GRAPH_MEMBER = "member";

  /**
   * Query Method을 사용하여 To-Do 목록 조회
   *
   * @param title       String
   * @param description String
   * @param completed   Boolean
   * @return List&lt;Todo&gt;
   */
  @EntityGraph(attributePaths = {ENTITY_GRAPH_MEMBER})
  List<TodoEntity> findAllByTitleContainingIgnoreCaseAndDescriptionContainingIgnoreCaseAndCompletedOrderByIdDesc(
      String title,
      String description,
      Boolean completed
  );

  // N + 1 문제
  // 1. fetch join -> inner join
  /*@Query("select t from Todo t join fetch t.member")
  @Override
  List<Todo> findAll();*/

  // 2. @EntityGraph -> left outer join
  @EntityGraph(attributePaths = {ENTITY_GRAPH_MEMBER})
  @Override
  List<TodoEntity> findAll();
}
