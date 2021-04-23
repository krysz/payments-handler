package com.krysz.paymentshandler.domain.model;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class BankAccount {
  @NonNull
  private final String iban;

  public BankAccount(String iban) {
    this.iban = iban;
  }
}
