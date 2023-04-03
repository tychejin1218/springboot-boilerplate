package com.example.boilerplate.common.dto;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Sort;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PageDto {

  private int size;
  private int page;
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
