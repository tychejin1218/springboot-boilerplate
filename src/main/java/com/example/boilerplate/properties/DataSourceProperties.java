package com.example.boilerplate.properties;

import lombok.Getter;
import lombok.ToString;

@ToString(exclude = "password")
@Getter
public class DataSourceProperties {

  protected String driverClassName;
  protected String jdbcUrl;
  protected String username;
  protected String password;
  protected String poolName;
  protected String connectionTestQuery;
  protected int maximumPoolSize;
  protected int minimumIdle;
  protected int idleTimeout;
  protected int maxLifeTime;
}
