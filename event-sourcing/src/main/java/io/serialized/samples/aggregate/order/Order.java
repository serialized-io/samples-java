package io.serialized.samples.aggregate.order;

import io.serialized.samples.aggregate.order.OrderState.OrderStatus;
import io.serialized.samples.aggregate.order.event.OrderCancelledEvent;
import io.serialized.samples.aggregate.order.event.OrderPaidEvent;
import io.serialized.samples.aggregate.order.event.OrderPlacedEvent;
import io.serialized.samples.aggregate.order.event.OrderShippedEvent;

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
    assertAcceptedAmount(amount);
    return new OrderPaidEvent(amount);
  }

  public OrderShippedEvent ship(String trackingNumber) {
    assertPaid();
    return new OrderShippedEvent(trackingNumber);
  }

  public OrderCancelledEvent cancel(String reason) {
    assertPlaced();
    return new OrderCancelledEvent(reason);
  }

  private void assertNotYetPlaced() {
    if (state.orderStatus != OrderStatus.NEW) throw new IllegalStateException("Expected order to be NEW!");
  }

  private void assertPlaced() {
    if (state.orderStatus != OrderStatus.PLACED) throw new IllegalStateException("Expected order to be PLACED!");
  }

  private void assertPaid() {
    if (state.orderStatus != OrderStatus.PAID) throw new IllegalStateException("Expected order to be PAID!");
  }

  private void assertAcceptedAmount(long amount) {
    if (state.orderAmount != amount) throw new IllegalArgumentException("Wrong amount!");
  }

}
