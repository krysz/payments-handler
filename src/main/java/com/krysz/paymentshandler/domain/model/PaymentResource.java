package com.krysz.paymentshandler.domain.model;

import java.math.BigDecimal;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class PaymentResource extends RepresentationModel<PaymentResource> {
  private final Long id;
  private final Long user;
  private final String bankAccount;
  private final String currency;
  private final BigDecimal amount;

  @JsonCreator
  public PaymentResource(@JsonProperty("id") Long id,
      @JsonProperty("user") Long user,
      @JsonProperty("bankAccount") String bankAccount,
      @JsonProperty("currency") String currency,
      @JsonProperty("amount") BigDecimal amount) {
    this.id = id;
    this.user = user;
    this.bankAccount = bankAccount;
    this.currency = currency;
    this.amount = amount;
  }
}
