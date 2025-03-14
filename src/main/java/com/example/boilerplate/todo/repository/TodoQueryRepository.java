package com.example.boilerplate.todo.repository;

import com.example.boilerplate.domain.entity.QMemberEntity;
import com.example.boilerplate.domain.entity.QTodoEntity;
import com.example.boilerplate.domain.entity.TodoEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
@Repository
public class TodoQueryRepository {

  private final JPAQueryFactory jpaQueryFactory;

  public List<TodoEntity> getTodos(TodoEntity todo) {

    QTodoEntity todoEntity = QTodoEntity.todoEntity;

    // 1. BooleanBuilder를 사용한 동적 쿼리 설정
    BooleanBuilder builder = new BooleanBuilder();

    if (StringUtils.hasText(todo.getTitle())) {
      builder.and(todoEntity.title.contains(todo.getTitle()));
    }
    if (StringUtils.hasText(todo.getDescription())) {
      builder.and(todoEntity.description.contains(todo.getDescription()));
    }
    if (todo.getCompleted() != null) {
      builder.and(todoEntity.completed.eq(todo.getCompleted()));
    }

    // 2. Projections를 사용한 객체 생성 방법
    // 2_1. 프로퍼티 접근(setter)
    /*return jpaQueryFactory
        .select(
            Projections.bean(
                Todo.class
                , qtodo.id.as("todoId")
                , qmember
                , qtodo.title
                , qtodo.description
                , qtodo.completed
            )
        )
        .from(qtodo)
        .leftJoin(qtodo.member, qmember)
        .where(builder)
        .orderBy(qtodo.id.desc())
        .fetch();*/

    // 2_2. 필드 직접 접근
    /*return jpaQueryFactory
        .select(
            Projections.fields(
                Todo.class
                , qtodo.id.as("todoId")
                , qmember
                , qtodo.title
                , qtodo.description
                , qtodo.completed
            )
        )
        .from(qtodo)
        .leftJoin(qtodo.member, qmember)
        .where(builder)
        .orderBy(qtodo.id.desc())
        .fetch();*/

    QMemberEntity memberEntity = QMemberEntity.memberEntity;

    // 2_3. 생성자 사용
    return jpaQueryFactory
        .select(
            Projections.constructor(
                TodoEntity.class,
                todoEntity.id,
                memberEntity,
                todoEntity.title,
                todoEntity.description,
                todoEntity.completed
            )
        )
        .from(todoEntity)
        .leftJoin(todoEntity.member, memberEntity)
        .where(builder)
        .orderBy(todoEntity.id.desc())
        .fetch();
  }

  public Page<TodoEntity> getTodos(TodoEntity todo, Pageable pageable) {

    QTodoEntity todoEntity = QTodoEntity.todoEntity;

    // 1. where 설정
    BooleanBuilder builder = new BooleanBuilder();

    if (StringUtils.hasText(todo.getTitle())) {
      builder.and(todoEntity.title.contains(todo.getTitle()));
    }
    if (StringUtils.hasText(todo.getDescription())) {
      builder.and(todoEntity.description.contains(todo.getDescription()));
    }
    if (todo.getCompleted() != null) {
      builder.and(todoEntity.completed.eq(todo.getCompleted()));
    }

    QMemberEntity memberEntity = QMemberEntity.memberEntity;

    // 2. list 조회
    List<TodoEntity> content = jpaQueryFactory
        .select(
            Projections.constructor(
                TodoEntity.class,
                todoEntity.id,
                memberEntity,
                todoEntity.title,
                todoEntity.description,
                todoEntity.completed
            )
        )
        .from(todoEntity)
        .leftJoin(todoEntity.member, memberEntity)
        .where(builder)
        .orderBy(todoEntity.id.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    // 3. count 조회
    JPAQuery<Long> countQuery = jpaQueryFactory
        .select(todoEntity.count())
        .from(todoEntity)
        .leftJoin(todoEntity.member, memberEntity)
        .where(builder);

    return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
  }
}
