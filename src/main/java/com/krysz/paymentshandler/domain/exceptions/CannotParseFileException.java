package com.krysz.paymentshandler.domain.exceptions;

import java.io.IOException;

public class CannotParseFileException extends RuntimeException {
  public CannotParseFileException(IOException cause, String message) {
    super(message, cause);
  }

  public CannotParseFileException(String message) {
    super(message);
  }
}
