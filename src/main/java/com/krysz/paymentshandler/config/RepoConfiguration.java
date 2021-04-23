package com.krysz.paymentshandler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.krysz.paymentshandler.adapters.CsvRepository;
import com.krysz.paymentshandler.adapters.OracleRepository;
import com.krysz.paymentshandler.domain.ports.PaymentRepository;

@Configuration
public class RepoConfiguration {

  @Profile("oracle")
  @Bean
  public PaymentRepository oracleRepository() {
    return new OracleRepository();
  }

  @Profile("!oracle")
  @Bean
  public PaymentRepository csvRepository() {
    return new CsvRepository();
  }
}
