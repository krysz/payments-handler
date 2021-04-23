package com.krysz.paymentshandler.api.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;

import com.krysz.paymentshandler.domain.model.BankAccount;
import com.krysz.paymentshandler.domain.model.PaymentCommand;

import io.vavr.control.Validation;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CommandValidator {

  public List<String> validateCommand(PaymentCommand pc) {
    List<Supplier<Validation<String, Object>>> list = new ArrayList<>();
    list.add(() -> amountCannotBeNagativeOrZero(pc.getAmount()));
    list.add(() -> ibanStartsWithCountryCode(pc.getBankAccount()));

    return list.stream().map(Supplier::get).filter(Validation::isInvalid).map(Validation::getError).collect(Collectors.toList());
  }

  private Validation<String, Object> ibanStartsWithCountryCode(BankAccount account) {
    return !account.getIban().matches("^[a-zA-Z][a-zA-Z].*") ?
        Validation.invalid("IBAN must start with country code") : Validation.valid(account);
  }

  private Validation<String, Object> amountCannotBeNagativeOrZero(MonetaryAmount amount) {
    return amount.isLessThanOrEqualTo(Money.of(0, amount.getCurrency())) ?
        Validation.invalid("Amount cannot be 0 or negative") : Validation.valid(amount);
  }
}
