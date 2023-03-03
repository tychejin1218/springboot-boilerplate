package com.example.boilerplate.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.boilerplate.common.constants.Constants;
import com.example.boilerplate.properties.MainDataSourceProperties;
import com.zaxxer.hikari.HikariDataSource;
import javax.annotation.Resource;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@ActiveProfiles("local")
@SpringBootTest
class MainDataSourceConfigTest {

  @Resource
  MainDataSourceProperties mainDataSourceProperties;

  @DisplayName("MainDataSource 설정 테스트")
  @Test
  void testMainDataSource(
      @Qualifier(Constants.MAIN_DATASOURCE) DataSource dataSource) {

    // Given
    String jdbcUrl = mainDataSourceProperties.getJdbcUrl();
    String username = mainDataSourceProperties.getUsername();

    // When
    try (HikariDataSource hikariDataSource = (HikariDataSource) dataSource) {

      // Then
      log.debug("hikariDataSource : [{}]", hikariDataSource);
      assertEquals(hikariDataSource.getJdbcUrl(), jdbcUrl);
      assertEquals(hikariDataSource.getUsername(), username);
    }
  }
}
