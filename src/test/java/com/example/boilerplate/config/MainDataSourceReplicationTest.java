package com.example.boilerplate.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.boilerplate.common.constants.Constants;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings({
    "PMD.AvoidAccessibilityAlteration"
})
@Slf4j
@ActiveProfiles("dev")
@SpringBootTest
@Disabled
class MainDataSourceReplicationTest {

  private static final String DETERMINE_CURRENT_LOOKUP_KEY = "determineCurrentLookupKey";

  @Transactional
  @Disabled("MainWriterDataSource Replication 설정 테스트")
  @Test
  void testMainWriterDataSourceReplication() throws Exception {

    // Given
    MainRoutingDataSource mainRoutingDataSource = new MainRoutingDataSource();

    // When
    Method declaredMethod =
        MainRoutingDataSource.class.getDeclaredMethod(DETERMINE_CURRENT_LOOKUP_KEY);
    declaredMethod.setAccessible(true);

    Object object = declaredMethod.invoke(mainRoutingDataSource);

    // Then
    log.debug("object : [{}]", object);
    assertEquals(object.toString(), Constants.MAIN_WRITER_KEY);
  }

  @Transactional(readOnly = true)
  @Disabled("MainReaderDataSource Replication 설정 테스트")
  @Test
  void testMainReaderDataSourceReplication() throws Exception {

    // Given
    MainRoutingDataSource mainRoutingDataSource = new MainRoutingDataSource();

    // When
    Method declaredMethod =
        MainRoutingDataSource.class.getDeclaredMethod(DETERMINE_CURRENT_LOOKUP_KEY);
    declaredMethod.setAccessible(true);

    Object object = declaredMethod.invoke(mainRoutingDataSource);

    // Then
    log.debug("object : [{}]", object);
    assertEquals(object.toString(), Constants.MAIN_READER_KEY);
  }
}
