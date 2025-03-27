package com.example.boilerplate.todo.repository;

import com.example.boilerplate.common.exception.ApiException;
import com.example.boilerplate.common.type.ApiStatus;
import com.example.boilerplate.domain.entity.QMemberEntity;
import com.example.boilerplate.domain.entity.QTodoEntity;
import com.example.boilerplate.domain.entity.TodoEntity;
import com.example.boilerplate.todo.dto.TodoDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/**
 * 할 일 관련 QueryDSL 기반의 커스텀 Repository 클래스
 */
@Slf4j
@RequiredArgsConstructor
@Repository
public class TodoQueryRepository {

  private final JPAQueryFactory jpaQueryFactory;
  private final ModelMapper modelMapper;

  /**
   * 검색 조건에 따라 할 일 목록을 조회하여 리스트로 반환
   *
   * @param todoRequest 검색 조건이 포함된 {@link TodoDto.Request} 객체
   * @return 할 일 목록을 담은 {@link TodoDto.Response} 리스트
   */
  public List<TodoDto.Response> getTodoList(TodoDto.Request todoRequest) {

    QTodoEntity todoEntity = QTodoEntity.todoEntity;
    QMemberEntity memberEntity = QMemberEntity.memberEntity;

    // 동적 조회 조건 생성
    BooleanBuilder builder = new BooleanBuilder();
    if (StringUtils.hasText(todoRequest.getTitle())) {
      builder.and(todoEntity.title.contains(todoRequest.getTitle()));
    }
    if (StringUtils.hasText(todoRequest.getDescription())) {
      builder.and(todoEntity.description.contains(todoRequest.getDescription()));
    }
    if (todoRequest.getCompleted() != null) {
      builder.and(todoEntity.completed.eq(todoRequest.getCompleted()));
    }

    // QueryDSL로 목록 조회
    List<TodoEntity> todoEntityList = jpaQueryFactory
        .selectFrom(todoEntity)
        .leftJoin(memberEntity)
        .on(todoEntity.memberId.eq(memberEntity.id))
        .where(builder)
        .orderBy(todoEntity.id.desc())
        .fetch();

    // Entity -> DTO 변환
    return toResponseList(todoEntityList);
  }

  /**
   * 검색 조건과 페이징 조건에 따라 할 일 목록을 조회하여 페이지 정보를 포함하여 반환
   *
   * @param pageRequest 검색 조건 및 페이징 조건이 포함된 {@link TodoDto.PageRequest} 객체
   * @return 페이징 처리된 {@link TodoDto.Response}의 {@link Page} 객체
   */
  public Page<TodoDto.Response> getPagedTodoList(TodoDto.PageRequest pageRequest) {

    QTodoEntity todoEntity = QTodoEntity.todoEntity;
    QMemberEntity memberEntity = QMemberEntity.memberEntity;

    // 동적 조회 조건 생성
    BooleanBuilder builder = new BooleanBuilder();
    if (StringUtils.hasText(pageRequest.getTitle())) {
      builder.and(todoEntity.title.contains(pageRequest.getTitle()));
    }
    if (StringUtils.hasText(pageRequest.getDescription())) {
      builder.and(todoEntity.description.contains(pageRequest.getDescription()));
    }
    if (pageRequest.getCompleted() != null) {
      builder.and(todoEntity.completed.eq(pageRequest.getCompleted()));
    }

    // QueryDSL로 목록 조회
    List<TodoEntity> todoEntityList = jpaQueryFactory
        .selectFrom(todoEntity)
        .leftJoin(memberEntity)
        .on(todoEntity.memberId.eq(memberEntity.id))
        .where(builder)
        .offset(pageRequest.pageRequest().getOffset())
        .limit(pageRequest.pageRequest().getPageSize())
        .orderBy(toOrderSpecifiers(pageRequest, todoEntity))
        .fetch();

    // 총 개수 조회
    long totalCount = jpaQueryFactory
        .select(todoEntity.count())
        .from(todoEntity)
        .leftJoin(memberEntity)
        .on(todoEntity.memberId.eq(memberEntity.id))
        .where(builder)
        .fetchOne();

    // Entity -> DTO 변환
    List<TodoDto.Response> todoList = toResponseList(todoEntityList);

    // Page 객체 반환
    return PageableExecutionUtils.getPage(todoList, pageRequest.pageRequest(), () -> totalCount);
  }

  /**
   * 페이징 요청에 따라 QueryDSL 정렬 조건(OrderSpecifier)을 생성
   *
   * <p>요청된 정렬 필드가 존재하지 않을 경우 예외가 발생
   *
   * @param pageRequest 페이징 및 정렬 조건이 포함된 {@link TodoDto.PageRequest} 객체
   * @param todoEntity  정렬 기준이 될 {@link QTodoEntity} 객체
   * @return 정렬 조건이 적용된 {@link OrderSpecifier} 배열
   */
  private OrderSpecifier<?>[] toOrderSpecifiers(
      TodoDto.PageRequest pageRequest,
      QTodoEntity todoEntity) {
    List<Sort.Order> orders = pageRequest.pageRequest().getSort().toList();

    // 정렬 조건이 없는 경우 기본 정렬 설정 (id,desc)
    if (orders.isEmpty()) {
      return new OrderSpecifier<?>[]{
          new OrderSpecifier<>(com.querydsl.core.types.Order.DESC, todoEntity.id)
      };
    }

    return orders.stream()
        .map(order -> {
          // Querydsl Order 설정
          com.querydsl.core.types.Order queryOrder =
              order.isAscending() ? com.querydsl.core.types.Order.ASC
                  : com.querydsl.core.types.Order.DESC;

          // 필드 매핑 (타입 명시적으로 캐스팅)
          return switch (order.getProperty()) {
            case "id" -> new OrderSpecifier<>(queryOrder, todoEntity.id);
            case "title" -> new OrderSpecifier<>(queryOrder, todoEntity.title);
            case "description" -> new OrderSpecifier<>(queryOrder, todoEntity.description);
            default -> {
              log.error("Invalid sort field: {}", order.getProperty());
              throw new ApiException(ApiStatus.METHOD_ARGUMENT_NOT_VALID);
            }
          };
        })
        .toArray(OrderSpecifier[]::new);
  }

  /**
   * 할 일 엔티티 리스트를 Response DTO 리스트로 변환
   *
   * @param todoEntityList 변환할 할 일 엔티티 {@link TodoEntity} 리스트
   * @return 변환된 {@link TodoDto.Response} 리스트
   */
  private List<TodoDto.Response> toResponseList(List<TodoEntity> todoEntityList) {
    return todoEntityList.stream()
        .map(entity -> modelMapper.map(entity, TodoDto.Response.class))
        .toList();
  }
}
