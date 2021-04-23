package com.krysz.paymentshandler.adapters;

import java.util.List;

import com.krysz.paymentshandler.domain.ports.PaymentRepository;
import com.krysz.paymentshandler.domain.ports.model.PaymentEntity;

public class OracleRepository implements PaymentRepository /*, CrudRepository<PaymentEntity, Long> */  {
  @Override
  public List<PaymentEntity> findAll() {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public PaymentEntity findById(Long id) {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public PaymentEntity save(PaymentEntity payment) {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public PaymentEntity update(PaymentEntity pc) {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public PaymentEntity deleteById(Long id) {
    throw new UnsupportedOperationException("Not implemented yet");
  }
}
