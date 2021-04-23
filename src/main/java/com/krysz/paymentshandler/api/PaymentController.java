package com.krysz.paymentshandler.api;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.krysz.paymentshandler.domain.model.PaymentCommand;
import com.krysz.paymentshandler.domain.model.PaymentResource;
import com.krysz.paymentshandler.domain.model.UpdatePaymentCommand;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

@RestController
@AllArgsConstructor
public class PaymentController {

  private final PaymentMapper paymentMapper;

  private final PaymentCommandProcessor paymentCommandProcessor;

  @GetMapping(value = "/api/payments", produces = "application/json")
  public ResponseEntity<List<PaymentResource>> getAllPayments() {
    List<PaymentResource> payment = paymentCommandProcessor.getPayments();
    return ResponseEntity.ok().body(payment);
  }

  @GetMapping(value = "/api/payments/{paymentId}", produces = "application/json")
  public ResponseEntity<PaymentResource> getPayment(@PathVariable Long paymentId) {
    PaymentResource payment = paymentCommandProcessor.getPayment(paymentId);
    return ResponseEntity.ok().body(payment);
  }

  @PostMapping(value = "/api/payments", produces = "application/json")
  public ResponseEntity<PaymentResource> storePayment(@RequestBody PaymentRequest request) {
     PaymentCommand paymentCommand = paymentMapper.map(request);
     return ResponseEntity.ok().body(paymentCommandProcessor.storePayment(paymentCommand));
  }

  @PutMapping(value = "/api/payments/{paymentId}", produces = "application/json")
  public ResponseEntity<PaymentResource> updatePayment(@PathVariable Long paymentId, @RequestBody PaymentRequest request) {
    UpdatePaymentCommand paymentCommand = new UpdatePaymentCommand(paymentMapper.map(request), paymentId);
    return ResponseEntity.ok().body(paymentCommandProcessor.updatePayment(paymentCommand));
  }

  @DeleteMapping(value = "/api/payments/{paymentId}", produces = "application/json")
  public ResponseEntity<PaymentResource> deletePayment(@PathVariable Long paymentId) {
    return ResponseEntity.ok().body(paymentCommandProcessor.deletePayment(paymentId));
  }

}

@Value
@AllArgsConstructor
class PaymentRequest {
  @NonNull String currency;
  @NonNull BigDecimal amount;
  @NonNull Long user;
  @NonNull String bankAccount;
}
