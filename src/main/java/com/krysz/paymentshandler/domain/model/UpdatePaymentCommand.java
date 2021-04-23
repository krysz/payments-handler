package com.krysz.paymentshandler.domain.model;

import lombok.Getter;
import lombok.NonNull;

public class UpdatePaymentCommand extends PaymentCommand{

  @Getter
  @NonNull
  private final PaymentId paymentId;

  public UpdatePaymentCommand(PaymentCommand baseCommand, @NonNull Long paymentId) {
    super(baseCommand.getAmount(), baseCommand.getUser(), baseCommand.getBankAccount());
    this.paymentId = new PaymentId(paymentId);
  }
}
