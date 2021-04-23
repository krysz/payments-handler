package com.krysz.paymentshandler.domain.model;

import lombok.NonNull;
import lombok.Value;

@Value
public class PaymentId {
  @NonNull Long paymentId;
}
