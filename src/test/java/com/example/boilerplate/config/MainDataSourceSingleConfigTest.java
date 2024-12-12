package com.example.boilerplate.config;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@ActiveProfiles("local")
@SpringBootTest
class MainDataSourceSingleConfigTest {

  @Value("${main.datasource.single.driver-class-name}")
  String driverClassName;

  @Value("${main.datasource.single.jdbc-url}")
  String jdbcUrl;

  @Value("${main.datasource.single.read-only}")
  Boolean readOnly;

  @Value("${main.datasource.single.username}")
  String username;

  @DisplayName("MainDataSource 설정 테스트")
  @Test
  void testMainDataSource(
      @Qualifier(MainDataSourceConfig.DATASOURCE_PREFIX
          + MainDataSourceConfig.DATASOURCE_BEAN_NAME) DataSource dataSource) {

    // Given & When
    try (HikariDataSource hikariDataSource = (HikariDataSource) dataSource) {
      // Then
      log.debug("hikariDataSource : [{}]", hikariDataSource);
      assertAll(
          () -> assertEquals(hikariDataSource.getDriverClassName(), driverClassName),
          () -> assertEquals(hikariDataSource.getJdbcUrl(), jdbcUrl),
          () -> assertEquals(hikariDataSource.isReadOnly(), readOnly),
          () -> assertEquals(hikariDataSource.getUsername(), username)
      );
    }
  }
}
