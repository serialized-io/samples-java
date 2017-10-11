package io.serialized.samples.aggregate.order;

import io.serialized.samples.aggregate.order.event.OrderCancelledEvent;
import io.serialized.samples.aggregate.order.event.OrderPaidEvent;
import io.serialized.samples.aggregate.order.event.OrderPlacedEvent;
import io.serialized.samples.aggregate.order.event.OrderShippedEvent;

import static io.serialized.samples.aggregate.order.event.OrderCancelledEvent.orderCancelled;
import static io.serialized.samples.aggregate.order.event.OrderPaidEvent.orderPaid;
import static io.serialized.samples.aggregate.order.event.OrderPlacedEvent.orderPlaced;
import static io.serialized.samples.aggregate.order.event.OrderShippedEvent.orderShipped;

public class Order {

  private final OrderStatus status;
  private final Amount orderAmount;

  public static Order createNewOrder() {
    return new Order(OrderStatus.NEW, Amount.ZERO);
  }

  public Order(OrderStatus status, Amount orderAmount) {
    this.status = status;
    this.orderAmount = orderAmount;
  }

  public OrderPlacedEvent place(CustomerId customerId, Amount orderAmount) {
    status.assertNotYetPlaced();
    return orderPlaced(customerId, orderAmount);
  }

  public OrderPaidEvent pay(Amount amount) {
    status.assertPlaced();
    Amount amountLeft = orderAmount.clear(amount);
    return orderPaid(this.orderAmount, amountLeft);
  }

  public OrderShippedEvent ship(TrackingNumber trackingNumber) {
    status.assertPaid();
    return orderShipped(trackingNumber);
  }

  public OrderCancelledEvent cancel(String reason) {
    status.assertPlaced();
    return orderCancelled(reason);
  }

}
