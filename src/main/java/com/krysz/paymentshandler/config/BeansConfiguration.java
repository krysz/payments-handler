package com.krysz.paymentshandler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import com.krysz.paymentshandler.adapters.CsvRepository;
import com.krysz.paymentshandler.adapters.OracleRepository;
import com.krysz.paymentshandler.api.PaymentMapper;
import com.krysz.paymentshandler.api.PaymentCommandProcessor;
import com.krysz.paymentshandler.api.logging.ApiRequestLoggingFilter;
import com.krysz.paymentshandler.api.logging.FilterConfiguration;
import com.krysz.paymentshandler.api.validator.CommandValidator;
import com.krysz.paymentshandler.domain.ports.PaymentRepository;

@Configuration
@Import(FilterConfiguration.class)
public class BeansConfiguration {

  @Bean
  public PaymentMapper paymentMapper() {
    return new PaymentMapper();
  }

  @Bean
  public CommandValidator commandValidator() {
    return new CommandValidator();
  }

  @Bean
  public PaymentCommandProcessor paymentProcessor(PaymentRepository repository, CommandValidator commandValidator) {
    return new PaymentCommandProcessor(commandValidator, repository);
  }

}
