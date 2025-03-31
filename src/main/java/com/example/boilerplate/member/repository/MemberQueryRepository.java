package com.example.boilerplate.member.repository;

import com.example.boilerplate.common.constants.Constants;
import com.example.boilerplate.common.exception.ApiException;
import com.example.boilerplate.common.type.ApiStatus;
import com.example.boilerplate.domain.entity.MemberEntity;
import com.example.boilerplate.domain.entity.QMemberEntity;
import com.example.boilerplate.domain.entity.QTodoEntity;
import com.example.boilerplate.member.dto.MemberDto;
import com.example.boilerplate.member.dto.MemberDto.Request;
import com.example.boilerplate.member.dto.MemberDto.Response;
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
 * 회원 관련 QueryDSL 기반의 커스텀 Repository 클래스
 */
@Slf4j
@RequiredArgsConstructor
@Repository
public class MemberQueryRepository {

  private final JPAQueryFactory jpaQueryFactory;
  private final ModelMapper modelMapper;

  /**
   * 검색 조건에 따라 회원 목록을 조회하여 리스트로 반환
   *
   * @param memberRequest 검색 조건이 포함된 {@link Request} 객체
   * @return 회원 목록을 담은 {@link Response} 리스트
   */
  public List<Response> selectMemberList(Request memberRequest) {

    QMemberEntity memberEntity = QMemberEntity.memberEntity;
    QTodoEntity todoEntity = QTodoEntity.todoEntity;

    // 동적 조회 조건 생성
    BooleanBuilder builder = new BooleanBuilder();
    if (StringUtils.hasText(memberRequest.getName())) {
      builder.and(memberEntity.name.contains(memberRequest.getName()));
    }
    if (StringUtils.hasText(memberRequest.getEmail())) {
      builder.and(memberEntity.email.contains(memberRequest.getEmail()));
    }

    // QueryDSL로 목록 조회
    List<MemberEntity> memberEntityList = jpaQueryFactory
        .selectFrom(memberEntity)
        .leftJoin(todoEntity)
        .on(memberEntity.id.eq(todoEntity.memberId))
        .where(builder)
        .orderBy(memberEntity.id.desc())
        .setHint(Constants.HIBERNATE_SQL_COMMENT, "MemberQueryRepository.getMemberList")
        .fetch();

    // Entity -> DTO 변환
    return toResponseList(memberEntityList);
  }

  /**
   * 검색 조건과 페이징 조건에 따라 회원 목록을 조회하여 페이지 정보를 포함하여 반환
   *
   * @param pageRequest 검색 조건 및 페이징 조건이 포함된 {@link MemberDto.PageRequest} 객체
   * @return 페이징 처리된 {@link MemberDto.Response}의 {@link Page} 객체
   */
  public Page<MemberDto.Response> selectPagedMemberList(MemberDto.PageRequest pageRequest) {

    QMemberEntity memberEntity = QMemberEntity.memberEntity;
    QTodoEntity todoEntity = QTodoEntity.todoEntity;

    // 동적 조회 조건 생성
    BooleanBuilder builder = new BooleanBuilder();
    if (StringUtils.hasText(pageRequest.getEmail())) {
      builder.and(memberEntity.email.contains(pageRequest.getEmail()));
    }
    if (StringUtils.hasText(pageRequest.getName())) {
      builder.and(memberEntity.name.contains(pageRequest.getName()));
    }

    // QueryDSL로 목록 조회
    List<MemberEntity> memberEntityList = jpaQueryFactory
        .selectFrom(memberEntity)
        .leftJoin(todoEntity)
        .on(memberEntity.id.eq(todoEntity.memberId))
        .where(builder)
        .offset(pageRequest.pageRequest().getOffset())
        .limit(pageRequest.pageRequest().getPageSize())
        .orderBy(toOrderSpecifiers(pageRequest, memberEntity))
        .setHint(Constants.HIBERNATE_SQL_COMMENT, "MemberQueryRepository.getPagedMemberList")
        .fetch();

    // 총 개수 조회
    long totalCount = jpaQueryFactory
        .select(memberEntity.count())
        .from(memberEntity)
        .leftJoin(todoEntity)
        .on(memberEntity.id.eq(todoEntity.memberId))
        .where(builder)
        .setHint(Constants.HIBERNATE_SQL_COMMENT, "MemberQueryRepository.getPagedMemberCount")
        .fetchOne();

    // Entity -> DTO 변환
    List<MemberDto.Response> memberList = toResponseList(memberEntityList);

    // Page 객체 반환
    return PageableExecutionUtils.getPage(memberList, pageRequest.pageRequest(), () -> totalCount);
  }

  /**
   * 페이징 요청에 따라 QueryDSL 정렬 조건(OrderSpecifier)을 생성
   *
   * <p>요청된 정렬 필드가 존재하지 않을 경우 예외가 발생
   *
   * @param pageRequest  페이징 및 정렬 조건이 포함된 {@link MemberDto.PageRequest} 객체
   * @param memberEntity 정렬 기준이 될 {@link QTodoEntity} 객체
   * @return 정렬 조건이 적용된 {@link OrderSpecifier} 배열
   */
  private OrderSpecifier<?>[] toOrderSpecifiers(
      MemberDto.PageRequest pageRequest,
      QMemberEntity memberEntity) {
    List<Sort.Order> orders = pageRequest.pageRequest().getSort().toList();

    // 정렬 조건이 없는 경우 기본 정렬 설정 (id,desc)
    if (orders.isEmpty()) {
      return new OrderSpecifier<?>[]{
          new OrderSpecifier<>(com.querydsl.core.types.Order.DESC, memberEntity.id)
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
            case "email" -> new OrderSpecifier<>(queryOrder, memberEntity.email);
            case "name" -> new OrderSpecifier<>(queryOrder, memberEntity.name);
            default -> {
              log.error("Invalid sort field: {}", order.getProperty());
              throw new ApiException(ApiStatus.METHOD_ARGUMENT_NOT_VALID);
            }
          };
        })
        .toArray(OrderSpecifier[]::new);
  }

  /**
   * 회원 엔티티 리스트를 Response DTO 리스트로 변환
   *
   * @param memberEntityList 변환할 회원 엔티티 {@link MemberEntity} 리스트
   * @return 변환된 {@link MemberDto.Response} 리스트
   */
  private List<MemberDto.Response> toResponseList(List<MemberEntity> memberEntityList) {
    return memberEntityList.stream()
        .map(entity -> modelMapper.map(entity, MemberDto.Response.class))
        .toList();
  }
}
