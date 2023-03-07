package com.example.boilerplate.sample.domain.repository;

import com.example.boilerplate.sample.domain.entity.Todo;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TodoRepository extends
    JpaRepository<Todo, Long>,
    TodoRepositoryCustom {

  // N + 1 문제
  // 1. fetch join -> inner join
  /*@Query("select t from Todo t join fetch t.member")
  List<Todo> findAll();*/

  // 2. @EntityGraph -> left outer join
  @EntityGraph(attributePaths = {"member"})
  List<Todo> findAll();
}
