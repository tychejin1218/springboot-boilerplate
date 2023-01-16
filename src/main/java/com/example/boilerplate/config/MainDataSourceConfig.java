package com.example.boilerplate.config;

import com.example.boilerplate.common.constants.Constants;
import com.example.boilerplate.properties.MainDataSourceProperties;
import com.example.boilerplate.properties.MainJpaProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Properties;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@AllArgsConstructor
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = Constants.BASE_PACKAGE + "*.domain.repository",
    entityManagerFactoryRef = "mainEntityManagerFactory",
    transactionManagerRef = "mainPlatformTransactionManager"
)
@Configuration
public class MainDataSourceConfig implements DataSourceConfig {

  private final MainDataSourceProperties mainDataSourceProperties;
  private final MainJpaProperties mainJpaProperties;

  @Primary
  @Bean(Constants.MAIN_DATASOURCE)
  public DataSource dataSource() {
    HikariConfig hikariConfig = hikariConfig(mainDataSourceProperties);
    hikariConfig.setInitializationFailTimeout(-1);
    return new HikariDataSource(hikariConfig);
  }

  @Primary
  @Bean("mainEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      @Qualifier(Constants.MAIN_DATASOURCE) DataSource dataSource) {
    LocalContainerEntityManagerFactoryBean entityManagerFactory
        = new LocalContainerEntityManagerFactoryBean();
    entityManagerFactory.setDataSource(dataSource);
    entityManagerFactory.setPackagesToScan(Constants.BASE_PACKAGE + "*.domain.entity");
    entityManagerFactory.setJpaVendorAdapter(this.jpaVendorAdapter());
    entityManagerFactory.setJpaProperties(this.jpaProperties());
    entityManagerFactory.setPersistenceUnitName("mainEntityManager");
    return entityManagerFactory;
  }

  private JpaVendorAdapter jpaVendorAdapter() {
    HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
    hibernateJpaVendorAdapter.setGenerateDdl(mainJpaProperties.getGenerateDdl());
    hibernateJpaVendorAdapter.setShowSql(mainJpaProperties.getShowSql());
    hibernateJpaVendorAdapter.setDatabasePlatform(mainJpaProperties.getDatabasePlatform());
    return hibernateJpaVendorAdapter;
  }

  private Properties jpaProperties() {
    Properties jpaProperties = new Properties();
    jpaProperties.setProperty(
        "hibernate.show_sql", mainJpaProperties.getHibernateShowSql());
    jpaProperties.setProperty(
        "hibernate.format_sql", mainJpaProperties.getHibernateFormatSql());
    jpaProperties.setProperty(
        "hibernate.highlight_sql", mainJpaProperties.getHibernateUseSqlComments());
    jpaProperties.setProperty(
        "hibernate.use_sql_comments", mainJpaProperties.getHibernateUseSqlComments());
    return jpaProperties;
  }

  @Primary
  @Bean("mainPlatformTransactionManager")
  public PlatformTransactionManager platformTransactionManager(
      @Qualifier("mainEntityManagerFactory")
          LocalContainerEntityManagerFactoryBean entityManagerFactory
  ) {
    JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
    jpaTransactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
    return jpaTransactionManager;
  }

  @Bean(name = "mainJdbcTemplate")
  public JdbcTemplate jdbcTemplate(
      @Qualifier(Constants.MAIN_DATASOURCE) DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }
}