package com.krysz.paymentshandler;

import org.springframework.http.ResponseEntity;

import com.krysz.paymentshandler.domain.model.PaymentResource;

public class ScenarioContext {

  private ResponseEntity<PaymentResource> response;

  public ResponseEntity<PaymentResource> getResponse() {
    return response;
  }
}
