package com.krysz.paymentshandler.api.validator;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.krysz.paymentshandler.domain.model.BankAccount;
import com.krysz.paymentshandler.domain.model.PaymentCommand;
import com.krysz.paymentshandler.domain.model.User;

import static org.junit.jupiter.api.Assertions.*;

class CommandValidatorTest {

  public static final int VALID_AMOUNT = 5;
  public static final String VALID_IBAN = "PL25";
  private final CommandValidator SUT = new CommandValidator();

  @Test
  public void shouldPassValidCommand() {
    PaymentCommand command = buildCommand(5, "PL25");

    var result = SUT.validateCommand(command);

    assertTrue(result.isEmpty());
  }

  @ParameterizedTest
  @ValueSource(longs = {Long.MIN_VALUE, -5, 0})
  public void shouldFailWhenAmountNegative(long amount) {
    PaymentCommand command = buildCommand(amount, VALID_IBAN);

    var result = SUT.validateCommand(command);

    assertEquals(1, result.size());
    assertEquals("Amount cannot be 0 or negative", result.get(0));
  }

  @ParameterizedTest
  @ValueSource(strings = {"25PL", "P1PL", "1PLP"})
  public void shouldFailWhenIbanDoesNotStartWithCountryCode(String iban) {
    PaymentCommand command = buildCommand(VALID_AMOUNT, iban);

    var result = SUT.validateCommand(command);

    assertEquals(1, result.size());
    assertEquals("IBAN must start with country code", result.get(0));
  }

  @Test
  public void shouldHasTwoErrorsIfBothValidationsFailed() {
    PaymentCommand command = buildCommand(0, "");

    var result = SUT.validateCommand(command);

    assertEquals(2, result.size());
    assertEquals("Amount cannot be 0 or negative", result.get(0));
    assertEquals("IBAN must start with country code", result.get(1));
  }

  private PaymentCommand buildCommand(long amount, String iban) {
    return new PaymentCommand(
        Money.of(amount, "USD"),
        new User(1L),
        new BankAccount(iban));
  }
}
