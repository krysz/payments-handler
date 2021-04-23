package com.krysz.paymentshandler.api;

import org.javamoney.moneta.Money;

import com.krysz.paymentshandler.domain.model.BankAccount;
import com.krysz.paymentshandler.domain.model.PaymentCommand;
import com.krysz.paymentshandler.domain.model.User;

public class PaymentMapper {

  public PaymentCommand map(PaymentRequest request) {
    Money money = Money.of(request.getAmount(), request.getCurrency());
    User user = new User(request.getUser());
    BankAccount bankAccount = new BankAccount(request.getBankAccount());

    return new PaymentCommand(money, user, bankAccount);
  }
}
