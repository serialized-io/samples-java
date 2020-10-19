package io.serialized.samples.orderservice.domain;

import io.serialized.client.aggregate.Event;
import io.serialized.samples.orderservice.domain.event.OrderCancelled;
import io.serialized.samples.orderservice.domain.event.OrderFullyPaid;
import io.serialized.samples.orderservice.domain.event.OrderPlaced;
import io.serialized.samples.orderservice.domain.event.OrderShipped;
import io.serialized.samples.orderservice.domain.event.PaymentReceived;

public class OrderState {

  private OrderStatus status;
  private Amount orderAmount;
  private OrderId orderId;
  private CustomerId customerId;

  public OrderState() {
    this.status = OrderStatus.NEW;
  }

  public OrderState handleOrderPlaced(Event<OrderPlaced> event) {
    this.status = OrderStatus.PLACED;
    this.orderAmount = new Amount(event.data().getOrderAmount());
    this.orderId = OrderId.fromUUID(event.data().getOrderId());
    this.customerId = CustomerId.fromUUID(event.data().getCustomerId());
    return this;
  }

  public OrderState handleOrderCancelled(Event<OrderCancelled> event) {
    this.status = OrderStatus.CANCELLED;
    return this;
  }

  public OrderState handleOrderShipped(Event<OrderShipped> event) {
    this.status = OrderStatus.SHIPPED;
    return this;
  }

  public OrderState handleOrderFullyPaid(Event<OrderFullyPaid> event) {
    this.status = OrderStatus.PAID;
    return this;
  }

  public OrderState handlePaymentReceived(Event<PaymentReceived> event) {
    return this;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public Amount getOrderAmount() {
    return orderAmount;
  }

  public OrderId getOrderId() {
    return orderId;
  }

  public CustomerId getCustomerId() {
    return customerId;
  }

}
