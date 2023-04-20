package com.example.boilerplate.config;

import java.io.IOException;
import java.io.StringReader;
import java.util.Scanner;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestServletWrapper extends HttpServletRequestWrapper {

  private String requestData;

  public RequestServletWrapper(HttpServletRequest request) {

    super(request);
    
    try (Scanner s = new Scanner(request.getInputStream()).useDelimiter("\\A")) {
      requestData = s.hasNext() ? s.next() : "";
    } catch (IOException e) {
      log.error("RequestServletWrapper:[{}]", e);
    }
  }

  @Override
  public ServletInputStream getInputStream() {

    StringReader reader = new StringReader(requestData);

    return new ServletInputStream() {

      ReadListener readListener;

      @Override
      public int read() throws IOException {
        return reader.read();
      }

      @Override
      public void setReadListener(ReadListener listener) {

        this.readListener = listener;

        try {
          if (!isFinished()) {
            readListener.onDataAvailable();
          } else {
            readListener.onAllDataRead();
          }
        } catch (IOException e) {
          log.error("getInputStream.setReadListener:", e);
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
          log.error("getInputStream.isFinished:[{}]", e);
        }
        return false;
      }
    };
  }
}
