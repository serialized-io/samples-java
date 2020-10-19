package io.serialized.samples.orderservice.domain.event;

import io.serialized.client.aggregate.Event;
import io.serialized.samples.orderservice.domain.Amount;
import io.serialized.samples.orderservice.domain.CustomerId;
import io.serialized.samples.orderservice.domain.OrderId;

import java.util.UUID;

import static io.serialized.client.aggregate.Event.newEvent;

public class OrderCancelled {

  private UUID orderId;
  private UUID customerId;
  private long orderAmount;
  private String reason;
  private long cancelledAt;

  public static Event<OrderCancelled> orderCancelled(OrderId orderId, CustomerId customerId, Amount orderAmount, String reason, long cancelledAt) {
    OrderCancelled event = new OrderCancelled();
    event.orderId = orderId.asUUID();
    event.customerId = customerId.asUUID();
    event.orderAmount = orderAmount.amount;
    event.reason = reason;
    event.cancelledAt = cancelledAt;
    return newEvent(event).build();
  }

  public UUID getOrderId() {
    return orderId;
  }

  public UUID getCustomerId() {
    return customerId;
  }

  public long getOrderAmount() {
    return orderAmount;
  }

  public String getReason() {
    return reason;
  }

  public long getCancelledAt() {
    return cancelledAt;
  }

}
