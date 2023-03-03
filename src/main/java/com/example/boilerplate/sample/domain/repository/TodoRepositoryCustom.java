package com.example.boilerplate.sample.domain.repository;

import com.example.boilerplate.sample.domain.entity.Todo;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TodoRepositoryCustom {

  /**
   * QueryDSL을 사용하여 To-Do 목록 조회
   */
  List<Todo> getTodos(Todo todo);

  /**
   * Pageable을 사용하여 To-Do 목록 조회
   */
  Page<Todo> getTodos(Todo todo, Pageable pageable);
}
