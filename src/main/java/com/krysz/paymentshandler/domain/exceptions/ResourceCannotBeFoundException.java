package com.krysz.paymentshandler.domain.exceptions;

public class ResourceCannotBeFoundException extends RuntimeException {
  public ResourceCannotBeFoundException(String message) {
    super(message);
  }
}
