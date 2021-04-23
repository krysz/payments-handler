package com.krysz.paymentshandler.api;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.krysz.paymentshandler.api.exceptions.RequestValidationException;
import com.krysz.paymentshandler.api.validator.CommandValidator;
import com.krysz.paymentshandler.domain.exceptions.ResourceCannotBeFoundException;
import com.krysz.paymentshandler.domain.model.PaymentCommand;
import com.krysz.paymentshandler.domain.model.PaymentResource;
import com.krysz.paymentshandler.domain.model.UpdatePaymentCommand;
import com.krysz.paymentshandler.domain.ports.PaymentRepository;
import com.krysz.paymentshandler.domain.ports.model.PaymentEntity;

import lombok.AllArgsConstructor;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@AllArgsConstructor
public class PaymentCommandProcessor {

  private final CommandValidator validator;

  private final PaymentRepository repository;

  public List<PaymentResource> getPayments() {
    List<PaymentEntity> payments = repository.findAll();

    return payments.stream().map(this::mapEntityToResource).collect(Collectors.toList());
  }

  public PaymentResource getPayment(Long paymentId) {
    PaymentEntity entity = repository.findById(paymentId);
    if (entity == null) {
      throw new ResourceCannotBeFoundException("Payment with id: " + paymentId + " cannot be found");
    }
    return mapEntityToResource(entity);
  }

  public PaymentResource storePayment(PaymentCommand pc) throws RequestValidationException{
    validateRequest(pc);
    PaymentEntity ent = preparePaymentEntity(pc, null);
    ent = repository.save(ent);
    return mapEntityToResource(ent);
  }

  public PaymentResource updatePayment(UpdatePaymentCommand pc) throws RequestValidationException{
    validateRequest(pc);
    PaymentEntity ent = preparePaymentEntity(pc, pc.getPaymentId().getPaymentId());
    ent = repository.update(ent);
    return mapEntityToResource(ent);
  }

  private void validateRequest(PaymentCommand pc) throws RequestValidationException {
    var validationResult = validator.validateCommand(pc);
    if (!validationResult.isEmpty()) {
      throw new RequestValidationException(validationResult);
    }
  }

  public PaymentResource deletePayment(Long paymentId) {
    PaymentEntity entity = repository.deleteById(paymentId);
    return createResourceFromEntity(entity);
  }

  private PaymentEntity preparePaymentEntity(PaymentCommand pc, Long id) {
    return new PaymentEntity(
        id,
        pc.getUser().getUserId(),
        pc.getBankAccount().getIban(),
        pc.getAmount().getCurrency().getCurrencyCode(),
        pc.getAmount().getNumber().numberValue(BigDecimal.class));
  }

  private PaymentResource mapEntityToResource(PaymentEntity ent) {
    PaymentResource resource = createResourceFromEntity(ent);
    addLinksToResource(ent, resource);
    return resource;
  }

  private PaymentResource createResourceFromEntity(PaymentEntity ent) {
    return new PaymentResource(ent.getId(),
        ent.getUser(),
        ent.getBankAccount(),
        ent.getCurrency(),
        ent.getAmount());
  }

  private void addLinksToResource(PaymentEntity ent, PaymentResource resource) {
    resource.add(linkTo(methodOn(PaymentController.class).getPayment(ent.getId())).withSelfRel());
    resource.add(linkTo(methodOn(PaymentController.class).updatePayment(ent.getId(), exampleRequest())).withRel("UPDATE"));
    resource.add(linkTo(methodOn(PaymentController.class).deletePayment(ent.getId())).withRel("DELETE"));
  }

  private PaymentRequest exampleRequest() {
    return new PaymentRequest("", BigDecimal.ZERO, 0L, "");
  }
}
