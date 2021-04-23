package com.krysz.paymentshandler.domain.model;

import javax.money.MonetaryAmount;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
@Getter
public class PaymentCommand {
  @NonNull
  private final MonetaryAmount amount;
  @NonNull
  private final User user;
  @NonNull
  private final BankAccount bankAccount;
}
