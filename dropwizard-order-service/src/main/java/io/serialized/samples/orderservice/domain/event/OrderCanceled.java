package io.serialized.samples.orderservice.domain.event;

import io.serialized.client.aggregate.Event;
import io.serialized.samples.orderservice.domain.CustomerId;
import io.serialized.samples.orderservice.domain.OrderId;

import java.util.UUID;

import static io.serialized.client.aggregate.Event.newEvent;

public class OrderCanceled {

  private UUID orderId;
  private UUID customerId;
  private String reason;
  private long canceledAt;

  public static Event<OrderCanceled> orderCanceled(OrderId orderId, CustomerId customerId, String reason, long canceledAt) {
    OrderCanceled event = new OrderCanceled();
    event.orderId = orderId.asUUID();
    event.customerId = customerId.asUUID();
    event.reason = reason;
    event.canceledAt = canceledAt;
    return newEvent(event).build();
  }

  public UUID getOrderId() {
    return orderId;
  }

  public UUID getCustomerId() {
    return customerId;
  }

  public String getReason() {
    return reason;
  }

  public long getCanceledAt() {
    return canceledAt;
  }

}
