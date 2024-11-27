package com.example.boilerplate.domain.repository;

import com.example.boilerplate.domain.entity.TodoDynamicEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoDynamicRepository extends
    JpaRepository<TodoDynamicEntity, Long> {

}
