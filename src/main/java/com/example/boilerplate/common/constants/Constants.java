package com.example.boilerplate.common.constants;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@SuppressWarnings({
    "PMD.DataClass"
})
public final class Constants {

  /**
   * 기본 패키지 경로
   */
  public static final String BASE_PACKAGE = "com.example.boilerplate";

  /**
   * HTTP GET 메서드를 나타내는 상수
   */
  public static final String GET = "GET";

  /**
   * HTTP POST 메서드를 나타내는 상수
   */
  public static final String POST = "POST";

  /**
   * 날짜를 "yyyy-MM-dd" 형식으로 포맷 (시간 제외)
   */
  public static final DateTimeFormatter DATE_FORMAT =
      DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault());

  /**
   * 날짜 및 시간 포맷 "yyyy-MM-dd HH:mm:ss" 형식으로 포맷 (밀리초 제외)
   */
  public static final DateTimeFormatter STANDARD_DATETIME_FORMAT =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

  /**
   * 날짜 및 시간 포맷 "yyyy-MM-dd HH:mm:ss.SSS" 형식으로 포맷 (밀리초 포함)
   */
  public static final DateTimeFormatter DETAILED_DATETIME_FORMAT =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());

  /**
   * 캐시 키 Prefix 값
   */
  public static final String BP_CACHE_PREFIX = "BP:";
}
