package com.krysz.paymentshandler.domain.ports.model;

import java.math.BigDecimal;

import com.opencsv.bean.CsvBindByPosition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaymentEntity {
  @CsvBindByPosition(position = 0)
  private Long id;
  @CsvBindByPosition(position = 1)
  private Long user;
  @CsvBindByPosition(position = 2)
  private String bankAccount;
  @CsvBindByPosition(position = 3)
  private String currency;
  @CsvBindByPosition(position = 4)
  private BigDecimal amount;
}
