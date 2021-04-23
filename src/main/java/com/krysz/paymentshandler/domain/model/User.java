package com.krysz.paymentshandler.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
@Getter
public class User {
  @NonNull
  private final Long userId;
}
