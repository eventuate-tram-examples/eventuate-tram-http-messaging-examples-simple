package io.eventuate.examples.tram.ordersandcustomers.common;

import io.eventuate.tram.events.common.DomainEvent;

public class OrderCreatedEvent implements DomainEvent {

  private OrderDetails orderDetails;

  public OrderCreatedEvent() {
  }

  public OrderCreatedEvent(OrderDetails orderDetails) {
    this.orderDetails = orderDetails;
  }

  public OrderDetails getOrderDetails() {
    return orderDetails;
  }
}
