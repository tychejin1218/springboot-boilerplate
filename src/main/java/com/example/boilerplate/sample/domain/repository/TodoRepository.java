package com.example.boilerplate.sample.domain.repository;

import com.example.boilerplate.sample.domain.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends
    JpaRepository<Todo, Long>,
    TodoRepositoryCustom {

}
