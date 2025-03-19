package com.example.boilerplate.common.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisComponent {

  private final StringRedisTemplate stringRedisTemplate;
  private final RedisTemplate<String, Object> objectRedisTemplate;
  private final RedisTemplate<String, Integer> integerRedisTemplate;
  private final ObjectMapper objectMapper;

  /**
   * 키에 해당하는 문자열을 저장
   *
   * @param key      저장할 키
   * @param value    저장할 값
   * @param duration 지속시간
   * @param timeunit 지속시간의 단위
   */
  public void setStringValue(String key, String value, long duration, TimeUnit timeunit) {
    try {
      ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
      valueOperations.set(key, value, duration, timeunit);
    } catch (Exception e) {
      log.error("setStringValue key: {}", key, e);
    }
  }

  /**
   * 키에 해당하는 문자열을 조회
   *
   * @param key 검색할 키
   * @return 해당 키로부터 가져온 값, 없거나 오류가 발생하면 null 반환
   */
  public String getStringValue(String key) {
    try {
      ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
      String value = valueOperations.get(key);
      if (StringUtils.hasText(value)) {
        return value;
      }
    } catch (Exception e) {
      log.error("getStringValue key: {}", key, e);
    }
    return null;
  }

  /**
   * 키에 해당하는 객체를 JSON 문자열 형태로 저장
   *
   * @param key      저장할 키
   * @param value    저장할 객체
   * @param duration 지속시간
   * @param timeunit 지속시간의 단위
   */
  public void setObjectValue(String key, Object value, long duration, TimeUnit timeunit) {
    try {
      ValueOperations<String, Object> valueOperations = objectRedisTemplate.opsForValue();
      valueOperations.set(key, value, duration, timeunit);
    } catch (Exception e) {
      log.error("setObjectValue key: {}", key, e);
    }
  }

  /**
   * 키에 해당하는 JSON 문자열을 조회하여 객체로 반환
   *
   * @param key           검색할 키
   * @param typeReference 반환할 객체의 타입 정보를 포함한 {@code TypeReference}
   * @param <T>           반환하는 객체의 타입
   * @return 해당 키로부터 가져온 객체, 없거나 오류가 발생하면 null 반환
   */
  public <T> T getObjectValue(String key, TypeReference<T> typeReference) {
    try {
      ValueOperations<String, Object> valueOperations = objectRedisTemplate.opsForValue();
      Object result = valueOperations.get(key);
      if (result == null) {
        return null;
      }
      return objectMapper.convertValue(result, typeReference);
    } catch (Exception e) {
      log.error("getObjectValue key: {}", key, e);
    }
    return null;
  }

  /**
   * 키에 해당하는 정수를 저장
   *
   * @param key      저장할 키
   * @param value    저장할 정수
   * @param duration 지속시간
   * @param timeunit 지속시간의 단위
   */
  public void setIntegerValue(String key, Integer value, long duration, TimeUnit timeunit) {
    try {
      ValueOperations<String, Integer> valueOperations = integerRedisTemplate.opsForValue();
      valueOperations.set(key, value, duration, timeunit);
    } catch (Exception e) {
      log.error("setIntegerValue key: {}", key, e);
    }
  }

  /**
   * 키에 해당하는 정수를 조회
   *
   * @param key 검색할 키
   * @return 해당 키로부터 가져온 정수 값, 없거나 오류가 발생하면 null 반환
   */
  public Integer getIntegerValue(String key) {
    try {
      ValueOperations<String, Integer> valueOperations = integerRedisTemplate.opsForValue();
      return valueOperations.get(key);
    } catch (Exception e) {
      log.error("getIntegerValue key: {}", key, e);
    }
    return null;
  }

  /**
   * 키에 해당하는 값을 삭제
   *
   * @param key 삭제할 키
   * @return 삭제가 성공했는지 여부
   */
  public boolean deleteKey(String key) {
    try {
      return Boolean.TRUE.equals(stringRedisTemplate.delete(key));
    } catch (Exception e) {
      log.error("deleteKey key : {}", key, e);
      return false;
    }
  }

  /**
   * Redis 캐시에서 데이터를 가져오거나, 캐시가 없을 경우 DB에서 조회 후 캐싱
   *
   * @param cacheKey      Redis 캐시 키
   * @param typeReference 반환할 객체의 타입 정보를 포함한 {@code TypeReference}
   * @param dbCallback    캐시에 데이터가 없는 경우 DB에서 데이터를 조회하는 콜백
   * @param ttl           캐시 만료 시간(Time-To-Live)
   * @param timeUnit      캐시 만료 시간 단위
   * @param <T>           반환 데이터의 타입
   * @return 캐시 데이터 또는 DB 조회 데이터를 반환
   */
  public <T> T getCacheOrDefault(
      String cacheKey,
      TypeReference<T> typeReference,
      Supplier<T> dbCallback,
      long ttl,
      TimeUnit timeUnit) {

    try {
      T cachedValue = getObjectValue(cacheKey, typeReference);
      if (cachedValue != null) {
        return cachedValue;
      }
    } catch (Exception e) {
      log.warn("Redis is unavailable. Falling back to DB. Error: {}", e.getMessage());
    }

    // 캐시에 값이 없거나 예외 발생 시 DB에서 값을 가져오고 Redis에 저장
    T value = dbCallback.get();
    setObjectValue(cacheKey, value, ttl, timeUnit);
    return value;
  }
}
