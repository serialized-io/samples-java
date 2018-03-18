package io.serialized.samples.order.domain;

import io.serialized.samples.order.domain.event.OrderCancelledEvent;
import io.serialized.samples.order.domain.event.OrderEvent;
import io.serialized.samples.order.domain.event.OrderPlacedEvent;
import io.serialized.samples.order.domain.event.OrderShippedEvent;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static io.serialized.samples.order.domain.event.OrderCancelledEvent.orderCancelled;
import static io.serialized.samples.order.domain.event.OrderFullyPaidEvent.orderFullyPaid;
import static io.serialized.samples.order.domain.event.OrderPlacedEvent.orderPlaced;
import static io.serialized.samples.order.domain.event.OrderShippedEvent.orderShipped;
import static io.serialized.samples.order.domain.event.PaymentExceededOrderAmountEvent.paymentExceededOrderAmount;
import static io.serialized.samples.order.domain.event.PaymentReceivedEvent.paymentReceived;

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

  public List<OrderEvent> pay(Amount amount) {
    status.assertPlaced();
    checkArgument(amount.isPositive());
    List<OrderEvent> events = new ArrayList<>();
    events.add(paymentReceived(amount));

    if (amount.largerThanEq(orderAmount)) {
      events.add(orderFullyPaid());
    }

    if (amount.largerThan(orderAmount)) {
      Amount difference = amount.difference(orderAmount);
      events.add(paymentExceededOrderAmount(difference));
    }

    return events;
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
