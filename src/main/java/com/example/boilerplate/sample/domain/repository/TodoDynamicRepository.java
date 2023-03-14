package com.example.boilerplate.sample.domain.repository;

import com.example.boilerplate.sample.domain.entity.TodoDynamic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoDynamicRepository extends
    JpaRepository<TodoDynamic, Long> {
//
}
