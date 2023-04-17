package com.example.boilerplate.config;

import org.apache.ibatis.session.AutoMappingUnknownColumnBehavior;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;

public interface DataSourceConfig {

  default Configuration configuration() {
    Configuration configuration = new Configuration();
    configuration.setDefaultStatementTimeout(30);
    configuration.setJdbcTypeForNull(JdbcType.VARCHAR);
    configuration.setMapUnderscoreToCamelCase(true);
    configuration.setCallSettersOnNulls(true);
    configuration.setAutoMappingUnknownColumnBehavior(AutoMappingUnknownColumnBehavior.WARNING);
    return configuration;
  }
}
