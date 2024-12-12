package com.example.boilerplate.config;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.io.StringReader;
import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link HttpServletRequest}를 확장하여 요청 본문(request body)을 여러 번 읽을 수 있도록 하는 커스텀 래퍼 클래스
 *
 * <p>이 클래스는 요청 본문 데이터를 내부 변수에 저장한 후, {@link ServletInputStream}을 통해 요청 데이터를 재사용할 수 있도록 처리
 */
@Slf4j
public class RequestServletWrapper extends HttpServletRequestWrapper {

  private String requestData;

  public RequestServletWrapper(HttpServletRequest request) {

    super(request);

    try (Scanner s = new Scanner(request.getInputStream()).useDelimiter("\\A")) {
      requestData = s.hasNext() ? s.next() : "";
    } catch (IOException e) {
      log.error("RequestServletWrapper", e);
    }
  }

  @Override
  public ServletInputStream getInputStream() {

    StringReader reader = new StringReader(requestData);

    return new ServletInputStream() {

      @Override
      public int read() throws IOException {
        return reader.read();
      }

      @Override
      public void setReadListener(ReadListener listener) {

        try {
          if (!isFinished()) {
            listener.onDataAvailable();
          } else {
            listener.onAllDataRead();
          }
        } catch (IOException e) {
          log.error("getInputStream.setReadListener", e);
        }
      }

      @Override
      public boolean isReady() {
        return isFinished();
      }

      @Override
      public boolean isFinished() {
        try (reader) {
          return reader.read() < 0;
        } catch (IOException e) {
          log.error("getInputStream.isFinished", e);
        }
        return false;
      }
    };
  }
}
