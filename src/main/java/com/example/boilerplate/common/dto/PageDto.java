package com.example.boilerplate.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Sort;

@Schema(description = "페이징 처리 요청 DTO")
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PageDto {

  @Schema(description = "한 페이지에 포함될 데이터 개수", example = "10", minimum = "1", maximum = "100")
  private int size;

  @Schema(description = "조회할 페이지 번호 (1부터 시작)", example = "1", minimum = "1", defaultValue = "1")
  private int page;

  @Schema(
      description = "정렬 조건 리스트. 필드명과 정렬 방향으로 구성됨. (예: 'name,asc', 'id,desc')",
      example = "[\"name,asc\", \"createdAt,desc\"]")
  private List<String> sorts;

  public void setPage(int page) {
    this.page = page <= 0 ? 1 : page;
  }

  public void setSize(int size) {
    int defaultSize = 10;
    int maxSize = 100;
    this.size = size > maxSize ? defaultSize : size;
  }

  public void setSorts(List<String> sorts) {
    this.sorts = sorts;
  }

  public org.springframework.data.domain.PageRequest pageRequest() {
    if (sorts == null || sorts.isEmpty()) {
      return org.springframework.data.domain.PageRequest.of(page - 1, size);
    }
    return org.springframework.data.domain.PageRequest.of(
        page - 1, size, Sort.by(getOrders(sorts)));
  }

  private List<Sort.Order> getOrders(List<String> sorts) {
    return sorts.stream()
        .map(sort ->
            new Sort.Order(
                Sort.Direction.valueOf(sort.split(",")[1].toUpperCase(Locale.ROOT)),
                sort.split(",")[0]))
        .collect(Collectors.toList());
  }
}
