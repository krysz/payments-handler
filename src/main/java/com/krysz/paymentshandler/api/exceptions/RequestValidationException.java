package com.krysz.paymentshandler.api.exceptions;

import java.util.List;

import lombok.Getter;

@Getter
public class RequestValidationException extends RuntimeException {
  private final List<String> errors;

  public RequestValidationException(List<String> errors) {
    super("Validation failed");
    this.errors = errors;
  }
}
