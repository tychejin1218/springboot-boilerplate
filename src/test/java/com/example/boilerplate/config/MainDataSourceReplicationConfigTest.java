package com.example.boilerplate.config;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@ActiveProfiles("dev")
@SpringBootTest
class MainDataSourceReplicationConfigTest {

  @Value("${main.datasource.writer.driver-class-name}")
  String writerDriverClassName;

  @Value("${main.datasource.writer.jdbc-url}")
  String writerJdbcUrl;

  @Value("${main.datasource.writer.username}")
  String writerUsername;

  @Value("${main.datasource.writer.password}")
  String writerPassword;

  @Value("${main.datasource.reader.driver-class-name}")
  String readerDriverClassName;

  @Value("${main.datasource.reader.jdbc-url}")
  String readerJdbcUrl;

  @Value("${main.datasource.reader.username}")
  String readerUsername;

  @Value("${main.datasource.reader.password}")
  String readerPassword;

  @DisplayName("Writer DataSource 설정 테스트")
  @Test
  void testWriterDataSource(
      @Qualifier(MainDataSourceConfig.DATASOURCE_PREFIX
          + "WriterDataSource") DataSource writerDataSource) {

    // Given & When
    try (HikariDataSource hikariDataSource = (HikariDataSource) writerDataSource) {
      // Then
      log.debug("writer hikariDataSource : [{}]", hikariDataSource);
      assertAll(
          () -> assertEquals(writerDriverClassName, hikariDataSource.getDriverClassName()),
          () -> assertEquals(writerJdbcUrl, hikariDataSource.getJdbcUrl()),
          () -> assertEquals(writerUsername, hikariDataSource.getUsername()),
          () -> assertEquals(writerPassword, hikariDataSource.getPassword())
      );
    }
  }

  @DisplayName("Reader DataSource 설정 테스트")
  @Test
  void testReaderDataSource(
      @Qualifier(MainDataSourceConfig.DATASOURCE_PREFIX
          + "ReaderDataSource") DataSource readerDataSource) {

    // Given & When
    try (HikariDataSource hikariDataSource = (HikariDataSource) readerDataSource) {
      // Then
      log.debug("reader hikariDataSource : [{}]", hikariDataSource);
      assertAll(
          () -> assertEquals(readerDriverClassName, hikariDataSource.getDriverClassName()),
          () -> assertEquals(readerJdbcUrl, hikariDataSource.getJdbcUrl()),
          () -> assertEquals(readerUsername, hikariDataSource.getUsername()),
          () -> assertEquals(readerPassword, hikariDataSource.getPassword())
      );
    }
  }

  @DisplayName("Routing DataSource 설정 테스트")
  @Test
  void testRoutingDataSource(
      @Qualifier(MainDataSourceConfig.DATASOURCE_PREFIX
          + MainDataSourceConfig.DATASOURCE_BEAN_NAME) DataSource dataSource) {

    // Given
    assertInstanceOf(LazyConnectionDataSourceProxy.class, dataSource);

    // When
    LazyConnectionDataSourceProxy proxy = (LazyConnectionDataSourceProxy) dataSource;
    DataSource targetDataSource = proxy.getTargetDataSource();

    // Then
    assertInstanceOf(AbstractRoutingDataSource.class, targetDataSource);
  }
}
