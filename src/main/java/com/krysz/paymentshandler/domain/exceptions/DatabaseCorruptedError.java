package com.krysz.paymentshandler.domain.exceptions;

import java.io.IOException;

public class DatabaseCorruptedError extends Error {
  public DatabaseCorruptedError(String message) {
    super(message);
  }

  public DatabaseCorruptedError(String message, IOException ex) {
    super(message, ex);
  }
}
