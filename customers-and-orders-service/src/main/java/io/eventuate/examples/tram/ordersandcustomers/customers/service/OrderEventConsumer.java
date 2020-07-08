package io.eventuate.examples.tram.ordersandcustomers.customers.service;

import io.eventuate.examples.tram.ordersandcustomers.common.OrderCreatedEvent;
import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class OrderEventConsumer {
  private Logger logger = LoggerFactory.getLogger(getClass());

  @Inject
  private CustomerService customerService;

  public DomainEventHandlers domainEventHandlers() {
    return DomainEventHandlersBuilder
            .forAggregateType("io.eventuate.examples.tram.ordersandcustomers.orders.domain.Order")
            .onEvent(OrderCreatedEvent.class, this::orderCreatedEventHandler)
            .build();
  }

  private void orderCreatedEventHandler(DomainEventEnvelope<OrderCreatedEvent> domainEventEnvelope) {

    OrderCreatedEvent orderCreatedEvent = domainEventEnvelope.getEvent();

    customerService.reserveCredit(Long.parseLong(domainEventEnvelope.getAggregateId()),
            orderCreatedEvent.getOrderDetails().getCustomerId(),
            orderCreatedEvent.getOrderDetails().getOrderTotal());
  }
}
