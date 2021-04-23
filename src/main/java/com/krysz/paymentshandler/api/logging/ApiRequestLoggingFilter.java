package com.krysz.paymentshandler.api.logging;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiRequestLoggingFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if (isAsyncDispatch(request)) {
      filterChain.doFilter(request, response);
    } else {
      doFilterWrapped(wrapRequest(request), wrapResponse(response), filterChain);
    }
  }

  protected void doFilterWrapped(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      filterChain.doFilter(request, response);
    } finally {
      logRequestAndResponse(request, response);
      response.copyBodyToResponse();
    }
  }

  protected void logRequestAndResponse(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) throws IOException {
    if (log.isInfoEnabled() && request.getRequestURI().startsWith("/api/")) {
      logRequest(request);
      logResponse(response);
    }
  }

  private void logRequest(ContentCachingRequestWrapper request) throws IOException {
    log.info("{}", createLogMessage(request));
  }

  private void logResponse(ContentCachingResponseWrapper response) throws IOException {
    log.info("{}", createLogMessage(response));
  }

  private ResponseLogMessage createLogMessage(ContentCachingResponseWrapper response) throws UnsupportedEncodingException {
    return new ResponseLogMessage(
        response.getStatus(),
        removeAllNewLines(new String(response.getContentAsByteArray(), response.getCharacterEncoding())));
  }

  private RequestLogMessage createLogMessage(ContentCachingRequestWrapper request) throws UnsupportedEncodingException {
    return new RequestLogMessage(
        request.getMethod(),
        getFullPath(request),
        request.getRemoteHost(),
        removeAllNewLines(new String(request.getContentAsByteArray(), request.getCharacterEncoding())));
  }

  private String getFullPath(ContentCachingRequestWrapper request) {
    if (request.getParameterMap().isEmpty()) {
      return request.getRequestURI();
    }
    return request.getRequestURI() + "?" +
        request.getParameterMap().keySet().stream()
            .map(key -> key + "=" + StringUtils.join(request.getParameterMap().get(key)))
            .collect(Collectors.joining("&"));
  }

  private static ContentCachingRequestWrapper wrapRequest(HttpServletRequest request) {
    return new ContentCachingRequestWrapper(request);
  }

  private static ContentCachingResponseWrapper wrapResponse(HttpServletResponse response) {
    return new ContentCachingResponseWrapper(response);
  }

  private String removeAllNewLines(String text) {
    return text.replaceAll("[\n\r]", "");
  }

  @Value
  static class RequestLogMessage {
    String httpMethod;
    String path;
    String clientIp;
    String body;
  }

  @Value
  static class ResponseLogMessage {
    int statusCode;
    String body;
  }
}

