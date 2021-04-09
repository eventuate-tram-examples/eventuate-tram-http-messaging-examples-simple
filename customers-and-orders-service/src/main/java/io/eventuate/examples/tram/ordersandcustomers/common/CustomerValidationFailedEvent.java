package io.eventuate.examples.tram.ordersandcustomers.common;

public class CustomerValidationFailedEvent extends AbstractCustomerOrderEvent {

  public CustomerValidationFailedEvent(Long orderId) {
    super(orderId);
  }

  public CustomerValidationFailedEvent() {
  }
}
