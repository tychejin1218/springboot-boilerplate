package com.example.boilerplate.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@ActiveProfiles("local")
@SpringBootTest(classes = {JasyptConfig.class})
class JasyptConfigurationTest {

  @Resource
  JasyptConfig jasyptConfiguration;

  @DisplayName("평문을 암호화한 후 복호화했을 때 최초의 평문과 같은지 테스트")
  @Test
  void testJasyptStringEncryptor() {

    StringEncryptor stringEncryptor = jasyptConfiguration.jasyptStringEncryptor();

    String message = "password1!";

    String messageEncrypt = stringEncryptor.encrypt(message);
    log.info("messageEncrypt:[{}]", messageEncrypt);

    String messageDecrypt = stringEncryptor.decrypt(messageEncrypt);
    log.info("messageDecrypt:[{}]", messageDecrypt);

    assertEquals(message, messageDecrypt, "평문을 암호화한 후 복호화했을 때 최초의 평문과 같음");
  }
}
