package com.example.boilerplate.config;

import com.example.boilerplate.properties.DataSourceProperties;
import com.zaxxer.hikari.HikariConfig;

public interface DataSourceConfig {

  default HikariConfig hikariConfig(DataSourceProperties properties) {
    HikariConfig hikariConfig = new HikariConfig();
    hikariConfig.setDriverClassName(properties.getDriverClassName());
    hikariConfig.setJdbcUrl(properties.getJdbcUrl());
    hikariConfig.setUsername(properties.getUsername());
    hikariConfig.setPassword(properties.getPassword());
    hikariConfig.setPoolName(properties.getPoolName());
    hikariConfig.setMaximumPoolSize(properties.getMaximumPoolSize());
    hikariConfig.setMinimumIdle(properties.getMinimumIdle());
    hikariConfig.setIdleTimeout(properties.getIdleTimeout());
    hikariConfig.setMaxLifetime(properties.getMaxLifeTime());
    hikariConfig.setConnectionTestQuery(properties.getConnectionTestQuery());
    return hikariConfig;
  }
}