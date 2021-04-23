package com.krysz.paymentshandler.domain.ports;

import java.util.List;

import com.krysz.paymentshandler.domain.model.PaymentCommand;
import com.krysz.paymentshandler.domain.ports.model.PaymentEntity;
import com.krysz.paymentshandler.domain.model.UpdatePaymentCommand;

public interface PaymentRepository {
  List<PaymentEntity> findAll();

  PaymentEntity findById(Long id);

  PaymentEntity save(PaymentEntity payment);

  PaymentEntity update(PaymentEntity pc);

  PaymentEntity deleteById(Long id);
}
