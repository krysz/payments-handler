package com.krysz.paymentshandler.api.logging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FilterConfiguration implements WebMvcConfigurer {

  @Bean
  public ApiRequestLoggingFilter logFilter() {
    return new ApiRequestLoggingFilter();
  }

}
