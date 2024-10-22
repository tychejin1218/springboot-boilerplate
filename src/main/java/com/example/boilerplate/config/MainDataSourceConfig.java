package com.example.boilerplate.config;

import com.example.boilerplate.common.constants.Constants;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Map;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@AllArgsConstructor
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = Constants.BASE_PACKAGE + ".*.domain.repository",
    entityManagerFactoryRef = MainDataSourceConfig.MAIN_DATASOURCE + "EntityManagerFactory",
    transactionManagerRef = MainDataSourceConfig.MAIN_DATASOURCE + "PlatformTransactionManager"
)
@Configuration
public class MainDataSourceConfig {

  public static final String MAIN_DATASOURCE = "mainDataSource";
  public static final String MAIN_WRITER_DATASOURCE = "mainWriterDatasource";
  public static final String MAIN_READER_DATASOURCE = "mainReaderDatasource";
  public static final String MAIN_ROUTING_DATASOURCE = "mainRoutingDatasource";
  public static final String MAIN_JPA = "mainJpa";
  public static final String MAIN_DATASOURCE_PROPERTY_PREFIX = "main.datasource";

  /**
   * 단일 데이터베이스 구성을 위한 데이터 소스 빈을 설정
   *
   * @return DataSource
   */
  @Profile("main_db_single")
  @Primary
  @Bean(MAIN_DATASOURCE)
  @ConfigurationProperties(prefix = MAIN_DATASOURCE_PROPERTY_PREFIX
      + ".single")
  public DataSource dataSource() {
    return DataSourceBuilder.create().type(HikariDataSource.class).build();
  }

  /**
   * 데이터베이스 리플리케이션 설정을 위한 내부 클래스
   */
  @Profile("main_db_replication")
  @Configuration
  class ReplicationConfig {

    /**
     * 쓰기 전용 데이터 소스를 설정
     *
     * @return DataSource
     */
    @Bean(MAIN_WRITER_DATASOURCE)
    @ConfigurationProperties(prefix = MAIN_DATASOURCE_PROPERTY_PREFIX
        + ".writer")
    public DataSource writerDataSource() {
      return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    /**
     * 읽기 전용 데이터 소스를 설정
     *
     * @return DataSource
     */
    @Primary
    @Bean(MAIN_READER_DATASOURCE)
    @ConfigurationProperties(prefix = MAIN_DATASOURCE_PROPERTY_PREFIX
        + ".reader")
    public DataSource readerDataSource() {
      return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    /**
     * 다중 데이터 소스를 설정
     *
     * @param writerDataSource 쓰기 전용 데이터 소스
     * @param readerDataSource 읽기 전용 데이터 소스
     * @return DataSource
     */
    @Primary
    @Bean(MAIN_ROUTING_DATASOURCE)
    public DataSource routingDataSource(
        @Qualifier(MAIN_WRITER_DATASOURCE) DataSource writerDataSource,
        @Qualifier(MAIN_READER_DATASOURCE) DataSource readerDataSource) {

      final String writerKey = "writerKey";
      final String readerKey = "readerKey";
      AbstractRoutingDataSource routingDataSource = new AbstractRoutingDataSource() {
        @Override
        protected Object determineCurrentLookupKey() {
          return TransactionSynchronizationManager.isCurrentTransactionReadOnly() ?
              readerKey : writerKey;
        }
      };

      Map<Object, Object> dataSourceMap = Map.of(
          writerKey, writerDataSource, readerKey, readerDataSource);

      routingDataSource.setTargetDataSources(dataSourceMap);
      routingDataSource.setDefaultTargetDataSource(writerDataSource);
      routingDataSource.afterPropertiesSet();

      return routingDataSource;
    }

    /**
     * 다중 데이터 소스를 설정
     *
     * @param routingDataSource 다중 데이터 소스
     * @return DataSource
     */
    @Primary
    @Bean(MAIN_DATASOURCE)
    public DataSource dataSourceReplication(
        @Qualifier(MAIN_ROUTING_DATASOURCE) DataSource routingDataSource) {
      return new LazyConnectionDataSourceProxy(routingDataSource);
    }
  }

  /**
   * JPA 속성을 설정
   *
   * @return JpaProperties
   */
  @Primary
  @Bean(MAIN_JPA + "JpaProperties")
  @ConfigurationProperties(prefix = "main.jpa")
  public JpaProperties jpaProperties() {
    return new JpaProperties();
  }

  /**
   * 엔티티 매니저 팩토리를 설정
   *
   * @param dataSource    데이터 소스
   * @param jpaProperties JPA 속성
   * @return LocalContainerEntityManagerFactoryBean
   */
  @Primary
  @Bean(MAIN_DATASOURCE + "EntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      @Qualifier(MAIN_DATASOURCE) DataSource dataSource,
      @Qualifier(MAIN_JPA + "JpaProperties") JpaProperties jpaProperties) {
    return this.entityManagerFactoryBuilder(jpaProperties)
        .dataSource(dataSource)
        .packages(Constants.BASE_PACKAGE + ".*.domain.entity")
        .properties(jpaProperties.getProperties())
        .persistenceUnit(MAIN_DATASOURCE + "EntityManager")
        .build();
  }

  /**
   * 엔티티 매니저 팩토리 빌더를 설정
   *
   * @param jpaProperties JPA 속성
   * @return EntityManagerFactoryBuilder
   */
  private EntityManagerFactoryBuilder entityManagerFactoryBuilder(JpaProperties jpaProperties) {
    HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
    jpaVendorAdapter.setGenerateDdl(jpaProperties.isGenerateDdl());
    jpaVendorAdapter.setShowSql(jpaProperties.isShowSql());
    jpaVendorAdapter.setDatabasePlatform(jpaProperties.getDatabasePlatform());
    return new EntityManagerFactoryBuilder(jpaVendorAdapter, jpaProperties.getProperties(), null);
  }

  /**
   * 트랜잭션 매니저를 설정
   *
   * @param entityManagerFactory 엔티티 매니저 팩토리
   * @return PlatformTransactionManager
   */
  @Primary
  @Bean(MAIN_DATASOURCE + "PlatformTransactionManager")
  public PlatformTransactionManager platformTransactionManager(
      @Qualifier(MAIN_DATASOURCE + "EntityManagerFactory")
      LocalContainerEntityManagerFactoryBean entityManagerFactory
  ) {
    JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
    jpaTransactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
    return jpaTransactionManager;
  }

  /**
   * JdbcTemplate 빈을 설정
   *
   * @param dataSource 데이터 소스
   * @return JdbcTemplate
   */
  @Bean(name = MAIN_DATASOURCE + "JdbcTemplate")
  public JdbcTemplate jdbcTemplate(
      @Qualifier(MAIN_DATASOURCE) DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }

  /**
   * JPA 및 QueryDSL 설정을 위한 내부 클래스
   */
  @Configuration
  class MainJpaQuerydslConfig {

    @PersistenceContext(unitName = MAIN_DATASOURCE + "EntityManager")
    private EntityManager mainEntityManager;

    /**
     * JPAQueryFactory 빈을 설정
     *
     * @return JPAQueryFactory
     */
    @Bean
    public JPAQueryFactory mainJpaQueryFactory() {
      return new JPAQueryFactory(mainEntityManager);
    }
  }
}
