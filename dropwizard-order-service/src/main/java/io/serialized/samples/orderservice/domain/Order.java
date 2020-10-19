package io.serialized.samples.orderservice.domain;

import io.serialized.client.aggregate.Event;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static io.serialized.samples.orderservice.domain.event.OrderCancelled.orderCancelled;
import static io.serialized.samples.orderservice.domain.event.OrderFullyPaid.orderFullyPaid;
import static io.serialized.samples.orderservice.domain.event.OrderPlaced.orderPlaced;
import static io.serialized.samples.orderservice.domain.event.OrderShipped.orderShipped;
import static io.serialized.samples.orderservice.domain.event.PaymentReceived.paymentReceived;
import static java.lang.System.currentTimeMillis;
import static java.util.Collections.singletonList;

public class Order {

  private final OrderStatus status;
  private final Amount orderAmount;
  private final OrderId orderId;
  private final CustomerId customerId;

  public Order(OrderState state) {
    this.status = state.getStatus();
    this.orderId = state.getOrderId();
    this.customerId = state.getCustomerId();
    this.orderAmount = state.getOrderAmount();
  }

  public List<Event<?>> place(OrderId orderId, CustomerId customerId, Amount orderAmount) {
    status.assertNotYetPlaced();
    return singletonList(orderPlaced(orderId, customerId, orderAmount, currentTimeMillis()));
  }

  public List<Event<?>> pay(Amount amount) {
    status.assertPlaced();
    checkArgument(amount.isPositive());
    List<Event<?>> events = new ArrayList<>();
    events.add(paymentReceived(orderId, customerId, amount, currentTimeMillis()));

    if (amount.largerThanEq(orderAmount)) {
      events.add(orderFullyPaid(orderId, customerId, currentTimeMillis()));
    }
    return events;
  }

  public List<Event<?>> ship(TrackingNumber trackingNumber) {
    status.assertPaid();
    return singletonList(orderShipped(orderId, customerId, trackingNumber, currentTimeMillis()));
  }

  public List<Event<?>> cancel(String reason) {
    status.assertPlaced();
    return singletonList(orderCancelled(orderId, customerId, orderAmount, reason, currentTimeMillis()));
  }

}
