package com.krysz.paymentshandler;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.krysz.paymentshandler.domain.model.PaymentResource;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.junit.platform.engine.Cucumber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

@Cucumber
public class PaymentsAcceptanceTestIT {

  @Autowired
  private RestTemplate restTemplate;

  private ResponseEntity<PaymentResource[]> response;

  private ResponseEntity<PaymentResource> singleResponse;

  private HttpClientErrorException exceptionResponse;

  @When("^call Payment-Handler in order to get payment with id (.*) from storage$")
  public void call_endpoint(Long id){
    try {
      singleResponse = restTemplate.getForEntity("http://localhost:8090/api/payments/" + id, PaymentResource.class);
    } catch(HttpClientErrorException exception) {
      exceptionResponse = exception;
    }
  }

  @When("^call Payment-Handler in order to get all payments from storage$")
  public void call_endpoint(){
    response = restTemplate.getForEntity("http://localhost:8090/api/payments", PaymentResource[].class);
  }

  @Then("^Payment-Handler respond successfully with body and HATEOAS links with one record with id (.*)$")
  public void payment_handler_respond_successfully_one_record(Long id) {
    assertEquals(HttpStatus.OK, singleResponse.getStatusCode());
    PaymentResource resource = singleResponse.getBody();

    assertNotNull(resource);
    assertEquals(id, resource.getId());
    assertLinksExists(resource);
  }

  @Then("^Payment-Handler respond successfully with body and HATEOAS links$")
  public void payment_handler_respond_successfully() {
    assertEquals(HttpStatus.OK, response.getStatusCode());

    PaymentResource resource = Objects.requireNonNull(response.getBody())[0];

    if (resource == null) {
      fail("Not found any record");
    }

    assertNotNull(Objects.requireNonNull(resource).getId());
    assertLinksExists(resource);
  }

  @Then("^Payment-Handler respond with 404 not found$")
  public void payment_handler_respond_with_404() {
    assertEquals(HttpStatus.NOT_FOUND, exceptionResponse.getStatusCode());
    assertThat(exceptionResponse.getResponseBodyAsString()).contains("cannot be found");
  }

  private void assertLinksExists(PaymentResource resource) {
    assertThat(resource.getRequiredLink("UPDATE").getHref()).endsWith("api/payments/"+ resource.getId());
    assertThat(resource.getRequiredLink("DELETE").getHref()).endsWith("api/payments/"+ resource.getId());
  }

}
