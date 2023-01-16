package com.example.boilerplate.properties;

public class MainDataSourceProperties extends DataSourceProperties {

  public void setDriverClassName(String driverClassName) {
    super.driverClassName = driverClassName;
  }

  public void setJdbcUrl(String jdbcUrl) {
    super.jdbcUrl = jdbcUrl;
  }

  public void setUsername(String username) {
    super.username = username;
  }

  public void setPassword(String password) {
    super.password = password;
  }

  public void setPoolName(String poolName) {
    super.poolName = poolName;
  }

  public void setMaximumPoolSize(int maximumPoolSize) {
    super.maximumPoolSize = maximumPoolSize;
  }

  public void setMinimumIdle(int minimumIdle) {
    super.minimumIdle = minimumIdle;
  }

  public void setIdleTimeout(int idleTimeout) {
    super.idleTimeout = idleTimeout;
  }

  public void setMaxLifeTime(int maxLifeTime) {
    super.maxLifeTime = maxLifeTime;
  }

  public void setConnectionTestQuery(String connectionTestQuery) {
    super.connectionTestQuery = connectionTestQuery;
  }
}