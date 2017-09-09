package io.serialized.samples.aggregate.order;

import io.serialized.samples.aggregate.order.event.OrderCancelledEvent;
import io.serialized.samples.aggregate.order.event.OrderPaidEvent;
import io.serialized.samples.aggregate.order.event.OrderPlacedEvent;

public class Order {

  private final OrderState state;

  public Order(OrderState state) {
    this.state = state;
  }

  public OrderPlacedEvent place(String customerId, long orderAmount) {
    assertNotYetPlaced();
    return new OrderPlacedEvent(customerId, orderAmount);
  }

  public OrderPaidEvent pay(long amount) {
    assertPlaced();
    assertNotCancelled();
    assertNotPaid();
    assertAcceptedAmount(amount);
    return new OrderPaidEvent();
  }

  public OrderCancelledEvent cancel(String reason) {
    assertPlaced();
    assertNotCancelled();
    assertNotPaid();
    return new OrderCancelledEvent(reason);
  }

  private void assertPlaced() {
    if (state.orderStatus != OrderState.OrderStatus.PLACED) throw new IllegalStateException("Order not placed!");
  }

  private void assertNotYetPlaced() {
    if (state.orderStatus != OrderState.OrderStatus.NEW) throw new IllegalStateException("Order already placed! ");
  }

  private void assertNotCancelled() {
    if (state.orderStatus == OrderState.OrderStatus.CANCELLED) throw new IllegalStateException("Order is cancelled!");
  }

  private void assertNotPaid() {
    if (state.paid) throw new IllegalStateException("Order is already paid!");
  }

  private void assertAcceptedAmount(long amount) {
    if (state.orderAmount != amount) throw new IllegalArgumentException("Wrong amount!");
  }

}
