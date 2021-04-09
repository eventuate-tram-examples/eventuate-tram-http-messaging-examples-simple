package io.eventuate.examples.tram.ordersandcustomers.orders.service;

import io.eventuate.examples.tram.ordersandcustomers.commondomain.Money;
import io.eventuate.examples.tram.ordersandcustomers.common.OrderApprovedEvent;
import io.eventuate.examples.tram.ordersandcustomers.common.OrderDetails;
import io.eventuate.examples.tram.ordersandcustomers.common.OrderRejectedEvent;
import io.eventuate.examples.tram.ordersandcustomers.orders.domain.Order;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.events.publisher.ResultWithEvents;
import io.micronaut.transaction.annotation.TransactionalAdvice;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

import static java.util.Collections.singletonList;

@Singleton
public class OrderService {

  @Inject
  private DomainEventPublisher domainEventPublisher;

  @PersistenceContext
  private EntityManager entityManager;

  @TransactionalAdvice
  public Order createOrder(OrderDetails orderDetails) {
    ResultWithEvents<Order> orderWithEvents = Order.createOrder(orderDetails);
    Order order = orderWithEvents.result;
    entityManager.persist(order);
    domainEventPublisher.publish(Order.class, order.getId(), orderWithEvents.events);
    return order;
  }

  public void approveOrder(Long orderId) {
    Order order = Optional.ofNullable(entityManager.find(Order.class, orderId))
            .orElseThrow(() -> new IllegalArgumentException(String.format("order with id %s not found", orderId)));
    order.noteCreditReserved();
    OrderDetails orderDetails = new OrderDetails(order.getOrderDetails().getCustomerId(), new Money(order.getOrderDetails().getOrderTotal().getAmount()));
    domainEventPublisher.publish(Order.class,
            orderId, singletonList(new OrderApprovedEvent(orderDetails)));
  }

  public void rejectOrder(Long orderId) {
    Order order = Optional
            .ofNullable(entityManager.find(Order.class, orderId))
            .orElseThrow(() -> new IllegalArgumentException(String.format("order with id %s not found", orderId)));
    order.noteCreditReservationFailed();
    OrderDetails orderDetails = new OrderDetails(order.getOrderDetails().getCustomerId(), new Money(order.getOrderDetails().getOrderTotal().getAmount()));
    domainEventPublisher.publish(Order.class,
            orderId, singletonList(new OrderRejectedEvent(orderDetails)));
  }
}
