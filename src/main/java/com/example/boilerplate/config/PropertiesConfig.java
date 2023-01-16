package com.example.boilerplate.config;

import com.example.boilerplate.properties.MainDataSourceProperties;
import com.example.boilerplate.properties.MainJpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PropertiesConfig {

  @Bean
  @ConfigurationProperties(prefix = "main.datasource")
  public MainDataSourceProperties mainDatasourceProperties() {
    return new MainDataSourceProperties();
  }

  @Bean
  @ConfigurationProperties(prefix = "main.jpa")
  public MainJpaProperties mainJpaProperties() {
    return new MainJpaProperties();
  }
}
