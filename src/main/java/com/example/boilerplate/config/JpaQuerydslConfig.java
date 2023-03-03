package com.example.boilerplate.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaQuerydslConfig {

  @PersistenceContext(unitName = "mainEntityManager")
  private EntityManager mainEntityManager;

  @Bean
  public JPAQueryFactory mainJpaQueryFactory() {
    return new JPAQueryFactory(mainEntityManager);
  }
}
