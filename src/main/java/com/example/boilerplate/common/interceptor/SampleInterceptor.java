package com.example.boilerplate.common.interceptor;

import com.example.boilerplate.common.constants.Constants;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component("SampleInterceptor")
public class SampleInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler
  ) {

    long startTime = System.currentTimeMillis();
    request.setAttribute("startTime", startTime);
    log.debug("[SAMPLE] Request URL - {} :: Start Time={}",
        request.getRequestURL().toString(), startTime);

    if (Constants.GET.equals(request.getMethod())
        && StringUtils.hasText(request.getQueryString())) {
      log.debug("[SAMPLE] Request URL - Params:{}", request.getQueryString());
    } else if (Constants.POST.equals(request.getMethod())) {
      try (ServletInputStream inputStream = request.getInputStream()) {
        String body = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        log.debug("[SAMPLE] Request URL - Body:{}", body);
      } catch (IOException e) {
        log.error("[SAMPLE] preHandle:", e);
      }
    }

    return true;
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request,
      HttpServletResponse response,
      Object object,
      Exception exception
  ) {
    long currentTime = System.currentTimeMillis();
    long startTime = (Long) request.getAttribute("startTime");
    log.debug("[SAMPLE] Request URL - {} :: End Time={}",
        request.getRequestURL().toString(), currentTime);
    log.debug("[SAMPLE] Request URL - {} :: Time Taken={}",
        request.getRequestURL().toString(), currentTime - startTime);
  }
}
