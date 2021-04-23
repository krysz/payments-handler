package com.krysz.paymentshandler.api.errorhandling;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.krysz.paymentshandler.domain.exceptions.ResourceCannotBeFoundException;
import com.krysz.paymentshandler.api.exceptions.RequestValidationException;

import lombok.Value;

@RestControllerAdvice
public class ExceptionControllerAdvice {

  @ExceptionHandler(RequestValidationException.class)
  private ResponseEntity<FailedResponse> handleValidationError(RequestValidationException failure) {
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
        .body(new FailedResponse(failure.getMessage(), failure.getErrors()));
  }

  @ExceptionHandler(ResourceCannotBeFoundException.class)
  private ResponseEntity<FailedResponse> handleNotFoundException(ResourceCannotBeFoundException failure) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new FailedResponse(
            failure.getClass().getSimpleName(),
            List.of(failure.getMessage())));
  }
}

@Value
class FailedResponse {
  String message;
  List<String> errors;
}

