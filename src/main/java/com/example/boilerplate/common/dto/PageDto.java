package com.example.boilerplate.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Schema(description = "페이징 처리 요청 DTO")
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PageDto {

  private static final int DEFAULT_PAGE = 1;
  private static final int DEFAULT_SIZE = 10;
  private static final int MAX_SIZE = 100;
  private static final int EXPECTED_SORT_PARTS = 2;

  @Schema(description = "한 페이지에 포함될 데이터 개수", example = "10", minimum = "1", maximum = "100")
  private int size;

  @Schema(description = "조회할 페이지 번호 (1부터 시작)", example = "1", minimum = "1", defaultValue = "1")
  private int page;

  @Schema(
      description = "정렬 조건 리스트. 필드명과 정렬 방향으로 구성됨. (예: 'title,desc', 'id,asc')",
      example = "[\"title,desc\", \"id,asc\"]")
  private List<String> sorts;

  public void setPage(Integer page) {
    this.page = (page == null || page <= 0) ? DEFAULT_PAGE : page;
  }

  public void setSize(Integer size) {
    this.size = (size == null || size <= 0 || size > MAX_SIZE) ? DEFAULT_SIZE : size;
  }

  public void setSorts(List<String> sorts) {
    this.sorts = sorts;
  }

  public PageRequest pageRequest() {
    setPage(this.page); // 기본값 설정
    setSize(this.size); // 기본값 설정

    if (sorts == null || sorts.isEmpty()) {
      return PageRequest.of(page - 1, size);
    }
    return PageRequest.of(page - 1, size, Sort.by(parseSortOrders(sorts)));
  }

  private List<Sort.Order> parseSortOrders(List<String> sorts) {
    return sorts.stream()
        .filter(Objects::nonNull) // Null 방지
        .map(sortStr -> {
          String[] parts = sortStr.split(",");
          if (parts.length != EXPECTED_SORT_PARTS) {
            throw new IllegalArgumentException("Invalid sort parameter: " + sortStr);
          }
          String property = parts[0];
          String direction = parts[1].toUpperCase(Locale.ROOT);
          Sort.Direction sortDirection = Sort.Direction.fromOptionalString(direction)
              .orElseThrow(
                  () -> new IllegalArgumentException("Invalid sort direction: " + direction));
          return new Sort.Order(sortDirection, property);
        })
        .collect(Collectors.toList());
  }
}
