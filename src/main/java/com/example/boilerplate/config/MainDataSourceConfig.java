package com.example.boilerplate.config;

import com.example.boilerplate.common.constants.Constants;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@AllArgsConstructor
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = Constants.BASE_PACKAGE + ".*.domain.repository",
    entityManagerFactoryRef = Constants.MAIN_DATASOURCE + "EntityManagerFactory",
    transactionManagerRef = Constants.MAIN_DATASOURCE + "PlatformTransactionManager"
)
@Configuration
public class MainDataSourceConfig implements DataSourceConfig {

  @Profile("main_db_single")
  @Primary
  @Bean(Constants.MAIN_DATASOURCE)
  @ConfigurationProperties(prefix = Constants.MAIN_DATASOURCE_PROPERTY_PREFIX + ".single")
  public DataSource dataSource() {
    return DataSourceBuilder.create().type(HikariDataSource.class).build();
  }

  @Profile("main_db_replication")
  @Configuration
  class ReplicationConfig {

    @Bean(Constants.MAIN_WRITER_DATASOURCE)
    @ConfigurationProperties(prefix = Constants.MAIN_DATASOURCE_PROPERTY_PREFIX + ".writer")
    public DataSource writerDataSource() {
      return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Primary
    @Bean(Constants.MAIN_READER_DATASOURCE)
    @ConfigurationProperties(prefix = Constants.MAIN_DATASOURCE_PROPERTY_PREFIX + ".reader")
    public DataSource readerDataSource() {
      return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Primary
    @Bean(Constants.MAIN_ROUTING_DATASOURCE)
    public DataSource routingDataSource(
        @Qualifier(Constants.MAIN_WRITER_DATASOURCE) DataSource writerDataSource,
        @Qualifier(Constants.MAIN_READER_DATASOURCE) DataSource readerDataSource) {

      MainRoutingDataSource mainRoutingDataSource = new MainRoutingDataSource();

      Map<Object, Object> dataSourceMap = new HashMap<>();
      dataSourceMap.put(Constants.MAIN_WRITER_KEY, writerDataSource);
      dataSourceMap.put(Constants.MAIN_READER_KEY, readerDataSource);

      mainRoutingDataSource.setTargetDataSources(dataSourceMap);
      mainRoutingDataSource.setDefaultTargetDataSource(writerDataSource);

      return mainRoutingDataSource;
    }

    @Primary
    @Bean(Constants.MAIN_DATASOURCE)
    public DataSource dataSourceReplication(
        @Qualifier(Constants.MAIN_ROUTING_DATASOURCE) DataSource routingDataSource) {
      return new LazyConnectionDataSourceProxy(routingDataSource);
    }
  }

  @Primary
  @Bean(Constants.MAIN_JPA + "JpaProperties")
  @ConfigurationProperties(prefix = Constants.MAIN_JPA_PREFIX)
  public JpaProperties jpaProperties() {
    return new JpaProperties();
  }

  @Primary
  @Bean(Constants.MAIN_DATASOURCE + "EntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      @Qualifier(Constants.MAIN_DATASOURCE) DataSource dataSource,
      @Qualifier(Constants.MAIN_JPA + "JpaProperties") JpaProperties jpaProperties) {
    return this.entityManagerFactoryBuilder(jpaProperties)
        .dataSource(dataSource)
        .packages(Constants.BASE_PACKAGE + ".*.domain.entity")
        .properties(jpaProperties.getProperties())
        .persistenceUnit(Constants.MAIN_DATASOURCE + "EntityManager")
        .build();
  }

  private EntityManagerFactoryBuilder entityManagerFactoryBuilder(JpaProperties jpaProperties) {
    HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
    jpaVendorAdapter.setGenerateDdl(jpaProperties.isGenerateDdl());
    jpaVendorAdapter.setShowSql(jpaProperties.isShowSql());
    jpaVendorAdapter.setDatabasePlatform(jpaProperties.getDatabasePlatform());
    return new EntityManagerFactoryBuilder(jpaVendorAdapter, jpaProperties.getProperties(), null);
  }

  @Primary
  @Bean(Constants.MAIN_DATASOURCE + "PlatformTransactionManager")
  public PlatformTransactionManager platformTransactionManager(
      @Qualifier(Constants.MAIN_DATASOURCE + "EntityManagerFactory")
      LocalContainerEntityManagerFactoryBean entityManagerFactory
  ) {
    JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
    jpaTransactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
    return jpaTransactionManager;
  }

  @Bean(name = Constants.MAIN_DATASOURCE + "JdbcTemplate")
  public JdbcTemplate jdbcTemplate(
      @Qualifier(Constants.MAIN_DATASOURCE) DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }

  @Configuration
  class MainJpaQuerydslConfig {

    @PersistenceContext(unitName = Constants.MAIN_DATASOURCE + "EntityManager")
    private EntityManager mainEntityManager;

    @Bean
    public JPAQueryFactory mainJpaQueryFactory() {
      return new JPAQueryFactory(mainEntityManager);
    }
  }
}
