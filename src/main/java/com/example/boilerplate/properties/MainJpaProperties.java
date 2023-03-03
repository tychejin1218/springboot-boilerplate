package com.example.boilerplate.properties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainJpaProperties {

  private Boolean generateDdl;
  private Boolean showSql;
  private String databasePlatform;
  private String hibernateShowSql;
  private String hibernateFormatSql;
  private String hibernateHighlightSql;
  private String hibernateUseSqlComments;
  private String hibernatePhysicalNamingStrategy;
}
