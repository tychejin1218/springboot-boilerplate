package com.example.boilerplate.sample.domain.repository;

import com.example.boilerplate.sample.domain.entity.QMember;
import com.example.boilerplate.sample.domain.entity.QTodo;
import com.example.boilerplate.sample.domain.entity.Todo;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public List<Todo> getTodos(Todo todo) {

    QTodo qtodo = QTodo.todo;

    // 1. BooleanBuilder를 사용한 동적 쿼리 설정
    BooleanBuilder builder = new BooleanBuilder();

    if (StringUtils.hasText(todo.getTitle())) {
      builder.and(qtodo.title.contains(todo.getTitle()));
    }
    if (StringUtils.hasText(todo.getDescription())) {
      builder.and(qtodo.description.contains(todo.getDescription()));
    }
    if (todo.getCompleted() != null) {
      builder.and(qtodo.completed.eq(todo.getCompleted()));
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

    QMember qmember = QMember.member;

    // 2_3. 생성자 사용
    return jpaQueryFactory
        .select(
            Projections.constructor(
                Todo.class,
                qtodo.id,
                qmember,
                qtodo.title,
                qtodo.description,
                qtodo.completed
            )
        )
        .from(qtodo)
        .leftJoin(qtodo.member, qmember)
        .where(builder)
        .orderBy(qtodo.id.desc())
        .fetch();
  }

  @Override
  public Page<Todo> getTodos(Todo todo, Pageable pageable) {

    QTodo qtodo = QTodo.todo;


    // 1. where 설정
    BooleanBuilder builder = new BooleanBuilder();

    if (StringUtils.hasText(todo.getTitle())) {
      builder.and(qtodo.title.contains(todo.getTitle()));
    }
    if (StringUtils.hasText(todo.getDescription())) {
      builder.and(qtodo.description.contains(todo.getDescription()));
    }
    if (todo.getCompleted() != null) {
      builder.and(qtodo.completed.eq(todo.getCompleted()));
    }

    QMember qmember = QMember.member;

    // 2. list 조회
    List<Todo> content = jpaQueryFactory
        .select(
            Projections.constructor(
                Todo.class,
                qtodo.id,
                qmember,
                qtodo.title,
                qtodo.description,
                qtodo.completed
            )
        )
        .from(qtodo)
        .leftJoin(qtodo.member, qmember)
        .where(builder)
        .orderBy(qtodo.id.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    // 3. count 조회
    JPAQuery<Long> countQuery = jpaQueryFactory
        .select(qtodo.count())
        .from(qtodo)
        .leftJoin(qtodo.member, qmember)
        .where(builder);

    return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
  }
}
