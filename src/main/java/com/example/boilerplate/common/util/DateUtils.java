package com.example.boilerplate.common.util;

import com.example.boilerplate.common.constants.Constants;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public final class DateUtils {

  public static final String YYYYMMDD = "yyyyMMdd";
  public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
  public static final String YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmssSSS";
  private static final AtomicLong LAST_TIME_MS = new AtomicLong();

  private DateUtils() {
  }

  /**
   * 현재 년을 YYYY 형식의 String 형으로 받아온다. <br>
   *
   * @return YYYY
   */
  public static String getCurrentYearString() {
    return String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1);
  }

  /**
   * 현재 월을 MM 형식의 String 형으로 받아온다. <br>
   *
   * @return MM
   */
  public static String getCurrentMonthString() {
    final String currMonth = String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1);
    return StringUtils.leftPad(currMonth, 2, Constants.ZERO_STR);
  }

  /**
   * 현재 날을 DD 형식의 String 형으로 받아온다. <br>
   *
   * @return DD
   */
  public static String getCurrentDayString() {
    final String currDay = String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
    return StringUtils.leftPad(currDay, 2, Constants.ZERO_STR);
  }

  /**
   * 현재 시간을 HH 형식의 String 형으로 받아온다. <br>
   *
   * @return HH
   */
  public static String getCurrentHourString() {
    final String currHour = String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
    return StringUtils.leftPad(currHour, 2, Constants.ZERO_STR);
  }

  /**
   * 현재 분을 mm 형식의 String 형으로 받아온다. <br>
   *
   * @return mm
   */
  public static String getCurrentMinuteString() {
    final String currMinute = String.valueOf(Calendar.getInstance().get(Calendar.MINUTE));
    return StringUtils.leftPad(currMinute, 2, Constants.ZERO_STR);
  }

  /**
   * 현재 초를 ss 형식의 String 형으로 받아온다. <br>
   *
   * @return ss
   */
  public static String getCurrentSecondString() {
    final String currSecond = String.valueOf(Calendar.getInstance().get(Calendar.SECOND));
    return StringUtils.leftPad(currSecond, 2, Constants.ZERO_STR);
  }

  /**
   * 문자형식의 날짜를 문자 패턴과 함께 Date 로 변환 한다.<br> Date date = DateUtils.getStringToDate("20101022",
   * "yyyyMMdd");<br>
   */
  public static Date getStringToDate(String str, String pattern) throws ParseException {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
    return simpleDateFormat.parse(str);
  }

  /**
   * 문자 형식의 날짜를 문자패턴으로 변환후 String으로 반환한다
   *
   * @param currentPattern 기존 패턴
   * @param changePattern  바꿀 패턴
   * @return 예외
   */
  public static String getStringToPatternChange(String str, String currentPattern,
      String changePattern) throws ParseException {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(currentPattern, Locale.getDefault());
    Date date = simpleDateFormat.parse(str);

    return getDateToString(date, changePattern);
  }

  /**
   * 해당 날짜 Date Object 를 패턴에 맞춘 문자형식의 날짜 형식으로 변경 .<br> String str = DateUtils.getDateToString(new
   * Date(), "yyyyMMddhhmmss");
   */
  public static String getDateToString(Date date, String pattern) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
    return simpleDateFormat.format(date);
  }

  /**
   * 두 날짜의 차이
   */
  public static long diffOfDate(String begin, String end) throws ParseException {
    SimpleDateFormat formatter = new SimpleDateFormat(YYYYMMDD, Locale.getDefault());
    Date beginDate = formatter.parse(begin);
    Date endDate = formatter.parse(end);
    long diff = endDate.getTime() - beginDate.getTime();
    return diff / (24 * 60 * 60 * 1000);
  }

  /**
   * 날자에서 받은값의일을 뺀 날짜
   *
   * @param date     날짜
   * @param pattern  패턴
   * @param minusDay 뺄 날짜
   */
  public static String getMinusDate(String date, String pattern, int minusDay)
      throws ParseException {
    SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
    Date formatDate = formatter.parse(date);
    long minusTime = (minusDay) * (24 * 60 * 60 * 1000);
    formatDate.setTime(formatDate.getTime() - minusTime);
    return formatter.format(formatDate);
  }

  /**
   * 특정 날짜로부터 주어진 날만큼 지난 날짜.
   *
   * @param date    날짜
   * @param pattern 패턴
   * @param plusDay 날짜로부터 지난 날짜
   * @param type    M:월, W:주, D:일
   */
  public static String getPlusDate(String date, String pattern, int plusDay, String type)
      throws ParseException {
    Calendar temp = Calendar.getInstance();
    temp.setTime(getStringToDate(date, pattern));
    StringBuffer sbDate = new StringBuffer();
    int typeField = 0;

    if ("M".equals(type)) {
      typeField = Calendar.MONTH;
    } else if ("W".equals(type)) {
      typeField = Calendar.WEEK_OF_MONTH;
    } else if ("D".equals(type)) {
      typeField = Calendar.DAY_OF_MONTH;
    }

    temp.add(typeField, plusDay);

    String tempYear = String.valueOf(temp.get(Calendar.YEAR));
    sbDate.append(tempYear);

    String tempMonth = String.valueOf(temp.get(Calendar.MONTH) + 1);
    sbDate.append(StringUtils.leftPad(tempMonth, 2, Constants.ZERO_STR));

    String tempDay = String.valueOf(temp.get(Calendar.DAY_OF_MONTH));
    sbDate.append(StringUtils.leftPad(tempDay, 2, Constants.ZERO_STR));

    return getStringToPatternChange(sbDate.toString(), YYYYMMDD, pattern);
  }

  /**
   * 현재날짜를 yyyyMMdd 형식으로 생성한다.
   */
  public static String getCurrentToday() {
    SimpleDateFormat formatter = new SimpleDateFormat(YYYYMMDD, Locale.getDefault());
    return formatter.format(new Date());
  }

  /**
   * 현재날짜를 pattern 형식으로 생성한다. (ex:yyyyMMdd)
   */
  public static String getCurrentToday(String pattern) {
    SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
    return formatter.format(new Date());
  }

  /**
   * 주차의 첫날짜를 리턴한다 ex) getFirstDayOfWeek(2013, 7, 2) return 7
   *
   * @param year  년
   * @param month 월
   * @param week  주차
   */
  public static String getFirstDayOfWeek(int year, int month, int week) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, month - 1);
    calendar.set(Calendar.WEEK_OF_MONTH, week);
    calendar.set(Calendar.DAY_OF_WEEK, 1);
    return String.valueOf(calendar.get(Calendar.DATE));
  }

  /**
   * 주차의 마지막 날짜를 리턴한다 ex) getLastDayOfWeek(2013, 7, 2) return 13
   *
   * @param year  년
   * @param month 월
   * @param week  주차
   */
  public static String getLastDayOfWeek(int year, int month, int week) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, month - 1);
    calendar.set(Calendar.WEEK_OF_MONTH, week);
    calendar.set(Calendar.DAY_OF_WEEK, 7);
    return String.valueOf(calendar.get(Calendar.DATE));
  }

  /**
   * 날짜의 월주차를 리턴한다
   *
   * @param year 년
   * @param mon  월
   * @param day  일
   */
  public static String getMonthWeekOfDay(int year, int mon, int day) {
    Calendar cal = Calendar.getInstance();
    cal.set(year, mon - 1, day);
    return String.valueOf(cal.get(Calendar.WEEK_OF_MONTH));
  }

  /**
   * 날짜의 연주차를 리턴한다
   *
   * @param year 년
   * @param mon  월
   * @param day  일
   */
  public static String getYearWeekOfDay(int year, int mon, int day) {
    Calendar cal = Calendar.getInstance();
    cal.set(year, mon - 1, day);
    return String.valueOf(cal.get(Calendar.WEEK_OF_YEAR));
  }

  /**
   * 날짜, 시간 유효성 체크 문자열 형식의 날짜 값과 Format 형태를 넘기면, 해당 날짜를 파싱해보고 파싱 에러가 나면 잘못된 날짜 파싱 에러가 나지 않으면 정상 날짜
   *
   * @param szDate   String : 체크할 날짜
   * @param szFormat String : 체크할 날짜 포맷
   * @return 날짜 유효성 통과 여부
   */
  public static boolean checkDate(String szDate, String szFormat) {
    boolean bresult = true;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat();

    simpleDateFormat.applyPattern(szFormat);
    simpleDateFormat.setLenient(false); // 엄밀하게 검사한다는 옵션 (반드시 있어야 한다)

    try {
      simpleDateFormat.parse(szDate);
    } catch (ParseException e) {
      bresult = false;
    }

    return bresult;
  }

  /**
   * 문자열 날짜의 요일을 가져온다 ex) getWeekOfDay("2014.08.02", "yyyy.MM.dd")
   *
   * @param textDate 대상날짜
   * @param pattern  대상패턴
   * @return 요일명
   */
  @SuppressWarnings("deprecation")
  public static String getWeekOfDay(String textDate, String pattern) {
    String result = "";
    final String[] weekDay = {"일", "월", "화", "수", "목", "금", "토"};

    try {
      Date date = getStringToDate(textDate, pattern);
      Calendar c = Calendar.getInstance();
      c.setTime(date);
      result = weekDay[c.get(Calendar.DAY_OF_WEEK) - 1];

    } catch (ParseException e) {
      log.error("++++++++++++++++++++ ParseException : {} ", e);
    }

    return result;
  }

  /**
   * 해당 월의 말일을 리턴
   */
  public static int getMonthLastDay(String textDate, String pattern) {
    int day = 0;
    try {
      Date date = getStringToDate(textDate, pattern);
      Calendar c = Calendar.getInstance();
      c.setTime(date);
      day = c.getActualMaximum(Calendar.DAY_OF_MONTH);
    } catch (ParseException e) {
      log.error("++++++++++++++++++++ ParseException : {} ", e);
    }

    return day;
  }

  /**
   * 문자 형식 날짜를 리턴
   */
  public static String getFormatDate(final String pattern) {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
  }

  /**
   * TimeMillis 형식 날짜를 문자 형식 날짜로 변환하여 리턴
   */
  public static String getTimeMillisToString(Long timeMillis, String pattern) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
    return simpleDateFormat.format(new Date(timeMillis));
  }

  /**
   * 문자 형식 날짜를 TimeMillis 형식으로 변환하여 리턴
   */
  public static Long getStringToTimeMillis(String textDate, String pattern) throws ParseException {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
    Date dateTimeMillis = simpleDateFormat.parse(textDate);
    return dateTimeMillis.getTime();
  }
}
