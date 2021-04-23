package com.krysz.paymentshandler.config;

import java.net.URL;
import java.util.Objects;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

import com.krysz.paymentshandler.ScenarioContext;
import com.krysz.paymentshandler.adapters.CsvRepository;
import com.krysz.paymentshandler.api.PaymentController;
import com.krysz.paymentshandler.api.errorhandling.ExceptionControllerAdvice;
import com.krysz.paymentshandler.domain.ports.PaymentRepository;

@Configuration
@EnableAutoConfiguration(exclude = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class })
@ComponentScan(basePackageClasses = { PaymentController.class, ExceptionControllerAdvice.class })
@Profile("IT")
public class IntegrationTestConfiguration {

  public static final URL TEST_DB = IntegrationTestConfiguration.class.getClassLoader().getResource("testDbIT.csv");

  @Bean
  @Primary
  public PaymentRepository csvRepository() {
    return new CsvRepository(Objects.requireNonNull(TEST_DB).getPath());
  }

  @Bean
  public ScenarioContext scenarioContext() {
    return new ScenarioContext();
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
