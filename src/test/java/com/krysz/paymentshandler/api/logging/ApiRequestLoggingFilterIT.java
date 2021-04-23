package com.krysz.paymentshandler.api.logging;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(classes = FilterConfiguration.class)
@AutoConfigureMockMvc
class ApiRequestLoggingFilterTestIT {

  private ListAppender<ILoggingEvent> listAppender;

  private final MockMvc mockMvc;

  public ApiRequestLoggingFilterTestIT(@Autowired MockMvc mockMvc) {
    this.mockMvc = mockMvc;
  }

  @BeforeEach
  public void setUp() {
    Logger logger = (Logger) LoggerFactory.getLogger(ApiRequestLoggingFilter.class);
    listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);
  }

  @Test
  public void shouldNotLogIfApiIsInEndpointName() throws Exception {
    //when
    mockMvc.perform(get("/dummyEndpoint/api/sth"));

    //then
    then(listAppender.list).isEmpty();
  }

  @Test
  public void shouldLogIfApiIsInEndpointName() throws Exception {
    //when
    mockMvc.perform(get("/api/payments"));

    //then
    then(listAppender.list)
        .isNotEmpty()
        .hasSize(2)
        .first()
        .asString()
        .contains("RequestLogMessage(httpMethod=GET");
  }
}
